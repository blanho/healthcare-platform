package com.healthcare.billing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice(basePackages = "com.healthcare.billing")
public class BillingExceptionHandler {

    @ExceptionHandler(InvoiceNotFoundException.class)
    public ProblemDetail handleInvoiceNotFound(InvoiceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Invoice Not Found");
        problem.setType(URI.create("https://healthcare.com/problems/invoice-not-found"));
        if (ex.getInvoiceId() != null) {
            problem.setProperty("invoiceId", ex.getInvoiceId().toString());
        }
        if (ex.getInvoiceNumber() != null) {
            problem.setProperty("invoiceNumber", ex.getInvoiceNumber());
        }
        return problem;
    }

    @ExceptionHandler(InvalidInvoiceOperationException.class)
    public ProblemDetail handleInvalidInvoiceOperation(InvalidInvoiceOperationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Invalid Invoice Operation");
        problem.setType(URI.create("https://healthcare.com/problems/invalid-invoice-operation"));
        if (ex.getInvoiceId() != null) {
            problem.setProperty("invoiceId", ex.getInvoiceId().toString());
        }
        if (ex.getCurrentStatus() != null) {
            problem.setProperty("currentStatus", ex.getCurrentStatus().name());
        }
        if (ex.getOperation() != null) {
            problem.setProperty("operation", ex.getOperation());
        }
        return problem;
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ProblemDetail handlePaymentNotFound(PaymentNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Payment Not Found");
        problem.setType(URI.create("https://healthcare.com/problems/payment-not-found"));
        if (ex.getPaymentId() != null) {
            problem.setProperty("paymentId", ex.getPaymentId().toString());
        }
        if (ex.getReferenceNumber() != null) {
            problem.setProperty("referenceNumber", ex.getReferenceNumber());
        }
        return problem;
    }

    @ExceptionHandler(PaymentProcessingException.class)
    public ProblemDetail handlePaymentProcessing(PaymentProcessingException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Payment Processing Failed");
        problem.setType(URI.create("https://healthcare.com/problems/payment-processing-failed"));
        if (ex.getPaymentId() != null) {
            problem.setProperty("paymentId", ex.getPaymentId().toString());
        }
        if (ex.getReferenceNumber() != null) {
            problem.setProperty("referenceNumber", ex.getReferenceNumber());
        }
        if (ex.getFailureCode() != null) {
            problem.setProperty("failureCode", ex.getFailureCode());
        }
        return problem;
    }

    @ExceptionHandler(ClaimNotFoundException.class)
    public ProblemDetail handleClaimNotFound(ClaimNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Insurance Claim Not Found");
        problem.setType(URI.create("https://healthcare.com/problems/claim-not-found"));
        if (ex.getClaimId() != null) {
            problem.setProperty("claimId", ex.getClaimId().toString());
        }
        if (ex.getClaimNumber() != null) {
            problem.setProperty("claimNumber", ex.getClaimNumber());
        }
        return problem;
    }

    @ExceptionHandler(ClaimProcessingException.class)
    public ProblemDetail handleClaimProcessing(ClaimProcessingException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Claim Processing Failed");
        problem.setType(URI.create("https://healthcare.com/problems/claim-processing-failed"));
        if (ex.getClaimId() != null) {
            problem.setProperty("claimId", ex.getClaimId().toString());
        }
        if (ex.getClaimNumber() != null) {
            problem.setProperty("claimNumber", ex.getClaimNumber());
        }
        if (ex.getStatus() != null) {
            problem.setProperty("status", ex.getStatus().name());
        }
        return problem;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Invalid Operation");
        problem.setType(URI.create("https://healthcare.com/problems/invalid-operation"));
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid Request");
        problem.setType(URI.create("https://healthcare.com/problems/invalid-request"));
        return problem;
    }
}
