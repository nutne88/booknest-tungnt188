package com.booknest.exception;

public class InsufficientCopiesException extends RuntimeException {

    public InsufficientCopiesException(String isbn, int requested, int available) {
        super("Not enough copies of book '%s': requested %d, available %d"
                .formatted(isbn, requested, available));
    }
}