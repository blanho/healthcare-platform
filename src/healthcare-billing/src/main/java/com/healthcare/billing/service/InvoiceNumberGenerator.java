package com.healthcare.billing.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InvoiceNumberGenerator {

    private static final String PREFIX = "INV";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final AtomicLong counter = new AtomicLong(0);
    private String currentDateKey;

    public synchronized String generate() {
        String today = LocalDate.now().format(DATE_FORMAT);

        if (!today.equals(currentDateKey)) {
            currentDateKey = today;
            counter.set(0);
        }

        long sequence = counter.incrementAndGet();
        return String.format("%s-%s-%05d", PREFIX, today, sequence);
    }

    public String generateForDate(LocalDate date, long sequence) {
        String dateStr = date.format(DATE_FORMAT);
        return String.format("%s-%s-%05d", PREFIX, dateStr, sequence);
    }
}
