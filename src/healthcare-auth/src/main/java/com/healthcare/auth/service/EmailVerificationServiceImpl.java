package com.healthcare.auth.service;

import com.healthcare.auth.domain.EmailVerificationToken;
import com.healthcare.auth.domain.User;
import com.healthcare.auth.domain.event.EmailVerifiedEvent;
import com.healthcare.auth.exception.AuthenticationException;
import com.healthcare.auth.exception.UserNotFoundException;
import com.healthcare.auth.repository.EmailVerificationTokenRepository;
import com.healthcare.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
class EmailVerificationServiceImpl implements EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void sendVerificationEmail(UUID userId) {
        log.debug("Sending verification email for user: {}", userId);

        User user = findUserOrThrow(userId);

        if (user.isEmailVerified()) {
            throw AuthenticationException.emailAlreadyVerified();
        }

        tokenRepository.invalidateUserTokens(userId);

        EmailVerificationToken.TokenWithHash tokenWithHash =
            EmailVerificationToken.create(user, user.getEmail());
        tokenRepository.save(tokenWithHash.entity());

        log.info("Verification email sent for user: {}", userId);
    }

    @Override
    @Transactional
    public void resendVerificationEmail(UUID userId) {
        log.debug("Resending verification email for user: {}", userId);
        sendVerificationEmail(userId);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        log.debug("Verifying email with token");

        String tokenHash = EmailVerificationToken.hash(token);
        EmailVerificationToken verificationToken = tokenRepository
            .findValidToken(tokenHash, Instant.now())
            .orElseThrow(AuthenticationException::emailVerificationTokenInvalid);

        User user = verificationToken.getUser();

        if (user.isEmailVerified()) {
            throw AuthenticationException.emailAlreadyVerified();
        }

        user.verifyEmail();
        userRepository.save(user);

        verificationToken.markAsUsed();
        tokenRepository.save(verificationToken);

        eventPublisher.publishEvent(
            EmailVerifiedEvent.of(user.getId(), user.getUsername(), user.getEmail())
        );

        log.info("Email verified for user: {}", user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailVerified(UUID userId) {
        return userRepository.findById(userId)
            .map(User::isEmailVerified)
            .orElse(false);
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.byId(userId));
    }
}
