package com.booknest.repository;

import com.booknest.domain.Book;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class BookRepository {

    private final EntityManager em;

    public BookRepository(EntityManager em) {
        this.em = em;
    }

    public Book save(Book book) {
        em.persist(book);
        return book;
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(em.find(Book.class, id));
    }

    public Optional<Book> findByIsbn(String isbn) {
        List<Book> results = em.createQuery(
                        "select b from Book b where b.isbn = :isbn", Book.class)
                .setParameter("isbn", isbn)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }

    public List<Book> findAll() {
        return em.createQuery("select b from Book b order by b.title", Book.class)
                .getResultList();
    }

    public List<Book> searchByTitleKeyword(String keyword) {
        return em.createQuery(
                        "select b from Book b where lower(b.title) like lower(:kw) order by b.title",
                        Book.class)
                .setParameter("kw", "%" + keyword + "%")
                .getResultList();
    }
}