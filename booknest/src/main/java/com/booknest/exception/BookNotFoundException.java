package com.booknest.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long bookId) {
        super("Book not found: id=" + bookId);
    }

    public BookNotFoundException(String isbn) {
        super("Book not found: isbn=" + isbn);
    }
}