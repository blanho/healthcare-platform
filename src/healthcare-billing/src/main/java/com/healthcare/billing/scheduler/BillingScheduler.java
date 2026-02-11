package com.healthcare.billing.scheduler;

import com.healthcare.billing.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BillingScheduler {

    private static final Logger log = LoggerFactory.getLogger(BillingScheduler.class);

    private final InvoiceService invoiceService;

    public BillingScheduler(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void markOverdueInvoices() {
        log.info("Starting scheduled task: Mark overdue invoices");
        try {
            int count = invoiceService.markOverdueInvoices();
            log.info("Completed marking {} invoices as overdue", count);
        } catch (Exception e) {
            log.error("Failed to mark overdue invoices", e);
        }
    }
}
