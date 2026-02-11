package com.healthcare.medicalrecord.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RecordNumberGenerator {

    private static final String PREFIX = "MR";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final AtomicInteger counter = new AtomicInteger(0);
    private String currentDate = "";

    public synchronized String generate() {
        String today = LocalDate.now().format(DATE_FORMAT);

        if (!today.equals(currentDate)) {
            currentDate = today;
            counter.set(0);
        }

        int sequence = counter.incrementAndGet();
        return String.format("%s-%s-%04d", PREFIX, today, sequence);
    }
}
