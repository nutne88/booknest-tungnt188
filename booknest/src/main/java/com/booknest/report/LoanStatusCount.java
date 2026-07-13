package com.booknest.report;

import com.booknest.domain.LoanStatus;

public record LoanStatusCount(LoanStatus status, Long count) {
}