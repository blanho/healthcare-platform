package com.healthcare.auth.service;

import com.healthcare.auth.domain.MfaBackupCode;
import com.healthcare.auth.domain.User;
import com.healthcare.auth.exception.AuthenticationException;
import com.healthcare.auth.exception.UserNotFoundException;
import com.healthcare.auth.repository.MfaBackupCodeRepository;
import com.healthcare.auth.repository.UserRepository;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
class MfaServiceImpl implements MfaService {

    private static final int BACKUP_CODE_COUNT = 10;
    private static final int SECRET_LENGTH = 32;

    private final UserRepository userRepository;
    private final MfaBackupCodeRepository backupCodeRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${healthcare.mfa.issuer:Healthcare Platform}")
    private String issuer;

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator(SECRET_LENGTH);
    private final TimeProvider timeProvider = new SystemTimeProvider();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1);
    private final CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

    @Override
    @Transactional(readOnly = true)
    public MfaSetupData generateMfaSetup(UUID userId) {
        log.debug("Generating MFA setup for user: {}", userId);

        User user = findUserOrThrow(userId);

        if (user.isMfaEnabled()) {
            throw AuthenticationException.mfaAlreadyEnabled();
        }

        String secret = secretGenerator.generate();

        QrData qrData = new QrData.Builder()
            .label(user.getEmail())
            .secret(secret)
            .issuer(issuer)
            .algorithm(HashingAlgorithm.SHA1)
            .digits(6)
            .period(30)
            .build();

        String qrCodeUri = qrData.getUri();

        return new MfaSetupData(secret, qrCodeUri, issuer, user.getEmail());
    }

    @Override
    @Transactional
    public List<String> enableMfa(UUID userId, String secret, String code) {
        log.debug("Enabling MFA for user: {}", userId);

        User user = findUserOrThrow(userId);

        if (user.isMfaEnabled()) {
            throw AuthenticationException.mfaAlreadyEnabled();
        }

        if (!codeVerifier.isValidCode(secret, code)) {
            throw AuthenticationException.mfaCodeInvalid();
        }

        user.enableMfa(secret);
        userRepository.save(user);

        List<String> backupCodes = generateAndSaveBackupCodes(user);

        log.info("MFA enabled for user: {}", userId);

        return backupCodes;
    }

    @Override
    @Transactional
    public void disableMfa(UUID userId, String password) {
        log.debug("Disabling MFA for user: {}", userId);

        User user = findUserOrThrow(userId);

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw AuthenticationException.invalidCredentials();
        }

        user.disableMfa();
        userRepository.save(user);

        backupCodeRepository.deleteByUserId(userId);

        log.info("MFA disabled for user: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyTotpCode(UUID userId, String code) {
        User user = findUserOrThrow(userId);

        if (!user.isMfaEnabled() || user.getMfaSecret() == null) {
            return false;
        }

        return codeVerifier.isValidCode(user.getMfaSecret(), code);
    }

    @Override
    @Transactional
    public boolean verifyBackupCode(UUID userId, String code, String ipAddress) {
        List<MfaBackupCode> unusedCodes = backupCodeRepository.findUnusedByUserId(userId);

        for (MfaBackupCode backupCode : unusedCodes) {
            if (backupCode.matches(code)) {
                backupCode.markAsUsed(ipAddress);
                backupCodeRepository.save(backupCode);
                log.info("Backup code used for user: {}", userId);
                return true;
            }
        }

        return false;
    }

    @Override
    @Transactional
    public List<String> regenerateBackupCodes(UUID userId, String password) {
        log.debug("Regenerating backup codes for user: {}", userId);

        User user = findUserOrThrow(userId);

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw AuthenticationException.invalidCredentials();
        }

        if (!user.isMfaEnabled()) {
            throw AuthenticationException.mfaNotEnabled();
        }

        backupCodeRepository.deleteByUserId(userId);

        List<String> backupCodes = generateAndSaveBackupCodes(user);

        log.info("Regenerated backup codes for user: {}", userId);

        return backupCodes;
    }

    @Override
    @Transactional(readOnly = true)
    public int getRemainingBackupCodeCount(UUID userId) {
        return backupCodeRepository.countUnusedByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMfaEnabled(UUID userId) {
        return userRepository.findById(userId)
            .map(User::isMfaEnabled)
            .orElse(false);
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.byId(userId));
    }

    private List<String> generateAndSaveBackupCodes(User user) {
        List<String> plainCodes = new ArrayList<>();

        for (int i = 0; i < BACKUP_CODE_COUNT; i++) {
            MfaBackupCode.CodeWithHash codeWithHash = MfaBackupCode.generate(user);
            backupCodeRepository.save(codeWithHash.entity());
            plainCodes.add(codeWithHash.plainCode());
        }

        return plainCodes;
    }
}
