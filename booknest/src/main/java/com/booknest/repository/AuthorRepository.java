package com.booknest.repository;

import com.booknest.domain.Author;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class AuthorRepository {

    private final EntityManager em;

    public AuthorRepository(EntityManager em) {
        this.em = em;
    }

    public Author save(Author author) {
        em.persist(author);
        return author;
    }

    public Optional<Author> findById(Long id) {
        return Optional.ofNullable(em.find(Author.class, id));
    }

    public List<Author> findAll() {
        return em.createQuery("select a from Author a order by a.fullName", Author.class)
                .getResultList();
    }

    public List<Author> searchByNameKeyword(String keyword) {
        return em.createQuery(
                        "select a from Author a where lower(a.fullName) like lower(:kw) order by a.fullName",
                        Author.class)
                .setParameter("kw", "%" + keyword + "%")
                .getResultList();
    }
}