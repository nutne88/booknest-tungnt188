package com.booknest.domain;

import jakarta.persistence.*;

@Entity
@Table(
        name = "books",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_books_isbn", columnNames = "isbn")
        },
        indexes = {
                @Index(name = "idx_books_isbn", columnList = "isbn")
        }
)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "isbn", nullable = false, length = 20)
    private String isbn;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "published_year")
    private Integer publishedYear;

    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookStatus status;

    protected Book() {
    }

    public Book(String isbn, String title, Integer publishedYear, Integer availableCopies) {
        this.isbn = isbn;
        this.title = title;
        this.publishedYear = publishedYear;
        this.availableCopies = availableCopies;
        this.status = BookStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public void increaseCopies(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        this.availableCopies += amount;
    }

    public void decreaseCopies(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (this.availableCopies - amount < 0) {
            throw new IllegalStateException("Not enough available copies for book " + isbn);
        }
        this.availableCopies -= amount;
    }

    @Override
    public String toString() {
        return "Book{id=%d, isbn='%s', title='%s'}".formatted(id, isbn, title);
    }
}
