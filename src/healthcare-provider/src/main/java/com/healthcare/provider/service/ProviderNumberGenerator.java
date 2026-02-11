package com.healthcare.provider.service;

import com.healthcare.provider.domain.ProviderType;
import com.healthcare.provider.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class ProviderNumberGenerator {

    private final ProviderRepository providerRepository;
    private final AtomicInteger sequence = new AtomicInteger(0);

    public String generateProviderNumber(ProviderType type) {
        String typeCode = type.getCode();
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));

        String providerNumber;
        do {
            int seq = sequence.incrementAndGet();
            providerNumber = String.format("%s%s%03d", typeCode, year, seq);
        } while (providerRepository.existsByProviderNumber(providerNumber));

        return providerNumber;
    }
}
