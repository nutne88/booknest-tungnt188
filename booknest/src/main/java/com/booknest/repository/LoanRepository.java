package com.booknest.repository;

import com.booknest.domain.Loan;
import com.booknest.report.LoanStatusCount;
import com.booknest.report.TopBorrowedBook;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LoanRepository {

    private final EntityManager em;

    public LoanRepository(EntityManager em) {
        this.em = em;
    }

    public List<Loan> findAllNaive() {
        return em.createQuery("select l from Loan l order by l.id", Loan.class)
                .getResultList();
    }

    public List<Loan> findAllWithDetails() {
        return em.createQuery(
                        """
                        select distinct l
                        from Loan l
                        join fetch l.member
                        left join fetch l.items i
                        left join fetch i.book
                        order by l.id
                        """,
                        Loan.class)
                .getResultList();
    }

    public Loan save(Loan loan) {
        em.persist(loan); // cascades LoanItems (CascadeType.ALL on Loan.items)
        return loan;
    }

    public Optional<Loan> findById(Long id) {
        return Optional.ofNullable(em.find(Loan.class, id));
    }

    public Optional<Loan> findDetailById(Long id) {
        List<Loan> results = em.createQuery(
                        """
                        select distinct l
                        from Loan l
                        join fetch l.member
                        left join fetch l.items i
                        left join fetch i.book
                        where l.id = :id
                        """,
                        Loan.class)
                .setParameter("id", id)
                .getResultList();
        return results.stream().findFirst();
    }

    public List<Loan> findActiveLoansByMemberEmail(String email) {
        return em.createQuery(
                        """
                        select l
                        from Loan l
                        join l.member m
                        where m.email = :email
                          and l.status <> com.booknest.domain.LoanStatus.RETURNED
                        order by l.dueDate
                        """,
                        Loan.class)
                .setParameter("email", email)
                .getResultList();
    }

    public List<Loan> findOverdueLoans(LocalDate asOf) {
        return em.createNamedQuery("Loan.findOverdue", Loan.class)
                .setParameter("asOf", asOf)
                .getResultList();
    }

    public List<LoanStatusCount> countByStatus() {
        return em.createQuery(
                        """
                        select new com.booknest.report.LoanStatusCount(l.status, count(l))
                        from Loan l
                        group by l.status
                        """,
                        LoanStatusCount.class)
                .getResultList();
    }

    public List<TopBorrowedBook> topBorrowedBooks(int limit) {
        return em.createQuery(
                        """
                        select new com.booknest.report.TopBorrowedBook(i.book.title, sum(i.quantity))
                        from LoanItem i
                        group by i.book.title
                        order by sum(i.quantity) desc
                        """,
                        TopBorrowedBook.class)
                .setMaxResults(limit)
                .getResultList();
    }
}