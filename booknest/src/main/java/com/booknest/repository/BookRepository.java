package com.booknest.repository;

import com.booknest.domain.Author;
import com.booknest.domain.Book;
import com.booknest.domain.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
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

    public List<Book> findAllPaged(int pageIndex, int pageSize) {
        return em.createQuery("select b from Book b order by b.title", Book.class)
                .setFirstResult(pageIndex * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public List<Book> searchByTitleKeywordPaged(String keyword, int pageIndex, int pageSize) {
        return em.createQuery(
                        "select b from Book b where lower(b.title) like lower(:kw) order by b.title",
                        Book.class)
                .setParameter("kw", "%" + keyword + "%")
                .setFirstResult(pageIndex * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public List<Book> findByCategory(Long categoryId) {
        return em.createQuery(
                        "select b from Book b where b.category.id = :categoryId order by b.title",
                        Book.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    public List<Book> findByAuthor(Long authorId) {
        return em.createQuery(
                        """
                        select distinct b
                        from Book b
                        join b.authors a
                        where a.id = :authorId
                        order by b.title
                        """,
                        Book.class)
                .setParameter("authorId", authorId)
                .getResultList();
    }

    public List<Book> findByCategoryNameNamedQuery(String categoryName) {
        return em.createNamedQuery("Book.findByCategoryName", Book.class)
                .setParameter("categoryName", categoryName)
                .getResultList();
    }

    public List<Book> findByAuthorNameNamedQuery(String authorName) {
        return em.createNamedQuery("Book.findByAuthorName", Book.class)
                .setParameter("authorName", authorName)
                .getResultList();
    }

    public List<Book> searchDynamic(String keyword,
                                    Long categoryId,
                                    Long authorId,
                                    Boolean onlyAvailable,
                                    int pageIndex,
                                    int pageSize) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        List<Predicate> predicates = new ArrayList<>();
        boolean needsDistinct = false;

        if (keyword != null && !keyword.isBlank()) {
            predicates.add(cb.like(cb.lower(book.get("title")), "%" + keyword.toLowerCase() + "%"));
        }

        if (categoryId != null) {
            // implicit join through the to-one path, same as the JPQL version above
            predicates.add(cb.equal(book.<Category>get("category").get("id"), categoryId));
        }

        if (authorId != null) {
            Join<Book, Author> authorJoin = book.join("authors");
            predicates.add(cb.equal(authorJoin.get("id"), authorId));
            needsDistinct = true;
        }

        if (Boolean.TRUE.equals(onlyAvailable)) {
            predicates.add(cb.greaterThan(book.get("availableCopies"), 0));
        }

        cq.select(book)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.asc(book.get("title")));

        if (needsDistinct) {
            cq.distinct(true);
        }

        return em.createQuery(cq)
                .setFirstResult(pageIndex * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }
}