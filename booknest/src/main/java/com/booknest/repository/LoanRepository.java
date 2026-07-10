package com.booknest.repository;

import com.booknest.domain.Loan;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class LoanRepository {

    private final EntityManager em;

    public LoanRepository(EntityManager em) {
        this.em = em;
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
}