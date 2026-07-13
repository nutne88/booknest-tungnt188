package com.booknest.service;

import com.booknest.domain.Loan;
import com.booknest.repository.LoanRepository;
import com.booknest.report.LoanStatusCount;
import com.booknest.report.TopBorrowedBook;
import com.booknest.util.Tx;

import java.time.LocalDate;
import java.util.List;

public class ReportService {

    public List<Loan> activeLoansByMemberEmail(String email) {
        return Tx.run(em -> new LoanRepository(em).findActiveLoansByMemberEmail(email));
    }

    public List<Loan> overdueLoans() {
        return Tx.run(em -> new LoanRepository(em).findOverdueLoans(LocalDate.now()));
    }

    public List<LoanStatusCount> countLoansByStatus() {
        return Tx.run(em -> new LoanRepository(em).countByStatus());
    }

    public List<TopBorrowedBook> topBorrowedBooks(int limit) {
        return Tx.run(em -> new LoanRepository(em).topBorrowedBooks(limit));
    }
}