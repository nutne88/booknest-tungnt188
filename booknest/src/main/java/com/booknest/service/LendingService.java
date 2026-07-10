package com.booknest.service;

import com.booknest.domain.Book;
import com.booknest.domain.Loan;
import com.booknest.domain.LoanItem;
import com.booknest.domain.LoanStatus;
import com.booknest.domain.Member;
import com.booknest.exception.BookNotFoundException;
import com.booknest.exception.InsufficientCopiesException;
import com.booknest.exception.InvalidInputException;
import com.booknest.exception.LoanNotFoundException;
import com.booknest.exception.MemberNotFoundException;
import com.booknest.repository.BookRepository;
import com.booknest.repository.LoanRepository;
import com.booknest.repository.MemberRepository;
import com.booknest.util.Tx;
import com.booknest.util.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class LendingService {

    private static final int DEFAULT_LOAN_PERIOD_DAYS = 14;

    public Loan lendBooks(Long memberId, Map<Long, Integer> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidInputException("A loan must contain at least one item");
        }

        return Tx.run(em -> {
            Member member = new MemberRepository(em).findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException(memberId));

            Loan loan = new Loan(LocalDate.now(), LocalDate.now().plusDays(DEFAULT_LOAN_PERIOD_DAYS));
            BookRepository bookRepository = new BookRepository(em);

            for (Map.Entry<Long, Integer> entry : items.entrySet()) {
                Long bookId = entry.getKey();
                Integer quantity = entry.getValue();

                if (quantity == null || quantity <= 0) {
                    throw new InvalidInputException("Quantity for book id " + bookId + " must be positive");
                }

                Book book = bookRepository.findById(bookId)
                        .orElseThrow(() -> new BookNotFoundException(bookId));

                if (book.getAvailableCopies() < quantity) {
                    throw new InsufficientCopiesException(book.getIsbn(), quantity, book.getAvailableCopies());
                }

                LoanItem loanItem = new LoanItem(quantity, book);
                validateOrThrow(loanItem);

                loan.addItem(loanItem);
                book.decreaseCopies(quantity);
            }

            validateOrThrow(loan);
            member.addLoan(loan);

            new LoanRepository(em).save(loan);
            return loan;
        });
    }

    public Loan returnLoan(Long loanId) {
        return Tx.run(em -> {
            Loan loan = new LoanRepository(em).findDetailById(loanId)
                    .orElseThrow(() -> new LoanNotFoundException(loanId));

            if (loan.getStatus() == LoanStatus.RETURNED) {
                throw new InvalidInputException("Loan " + loanId + " was already returned");
            }

            for (LoanItem item : loan.getItems()) {
                item.getBook().increaseCopies(item.getQuantity());
            }
            loan.markReturned(LocalDateTime.now());

            return loan;
        });
    }
    public Loan viewLoanDetail(Long loanId) {
        return Tx.run(em -> new LoanRepository(em).findDetailById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId)));
    }

    private static <T> void validateOrThrow(T entity) {
        var violations = ValidationUtil.validate(entity);
        if (!violations.isEmpty()) {
            throw new InvalidInputException(String.join("; ", violations));
        }
    }
}