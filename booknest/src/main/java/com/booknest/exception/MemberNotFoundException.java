package com.booknest.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(Long memberId) {
        super("Member not found: id=" + memberId);
    }

    public MemberNotFoundException(String email) {
        super("Member not found: email=" + email);
    }
}