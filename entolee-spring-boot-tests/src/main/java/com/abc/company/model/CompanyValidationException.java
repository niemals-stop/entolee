package com.abc.company.model;

public class CompanyValidationException extends RuntimeException {
    CompanyValidationException(String message) {
        super(message);
    }
}
