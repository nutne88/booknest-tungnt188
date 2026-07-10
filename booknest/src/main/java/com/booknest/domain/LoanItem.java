package com.booknest.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "loan_items")
public class LoanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Positive
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    protected LoanItem() {
    }

    public LoanItem(Integer quantity) {
        this.quantity = quantity;
    }

    public LoanItem(Integer quantity, Book book) {
        this.quantity = quantity;
        this.book = book;
    }

    public Long getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Loan getLoan() {
        return loan;
    }

    void assignLoan(Loan loan) {
        this.loan = loan;
    }

    public Book getBook() {
        return book;
    }

    public void assignBook(Book book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return "LoanItem{id=%d, quantity=%d}".formatted(id, quantity);
    }
}