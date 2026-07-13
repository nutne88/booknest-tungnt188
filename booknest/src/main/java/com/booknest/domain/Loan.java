package com.booknest.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQuery(
        name = "Loan.findOverdue",
        query = "select l from Loan l where l.status = com.booknest.domain.LoanStatus.ACTIVE "
                + "and l.dueDate < :asOf order by l.dueDate"
)
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LoanStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LoanItem> items = new ArrayList<>();

    protected Loan() {
    }

    public Loan(LocalDate loanDate, LocalDate dueDate) {
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.status = LoanStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public Member getMember() {
        return member;
    }

    void assignMember(Member member) {
        this.member = member;
    }

    public List<LoanItem> getItems() {
        return items;
    }

    public void addItem(LoanItem item) {
        items.add(item);
        item.assignLoan(this);
    }

    public void removeItem(LoanItem item) {
        items.remove(item);
        item.assignLoan(null);
    }

    public void markReturned(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
        this.status = LoanStatus.RETURNED;
    }

    public void markOverdue() {
        if (this.status == LoanStatus.ACTIVE) {
            this.status = LoanStatus.OVERDUE;
        }
    }

    @Override
    public String toString() {
        return "Loan{id=%d, loanDate=%s, dueDate=%s, status=%s}"
                .formatted(id, loanDate, dueDate, status);
    }
}