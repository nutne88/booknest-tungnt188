package com.booknest.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

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

    @NotBlank
    @Size(max = 20)
    @Column(name = "isbn", nullable = false, length = 20)
    private String isbn;

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "published_year")
    private Integer publishedYear;

    @NotNull
    @PositiveOrZero
    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

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

    public Category getCategory() {
        return category;
    }

    public void assignCategory(Category category) {
        this.category = category;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void addAuthor(Author author) {
        authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author) {
        authors.remove(author);
        author.getBooks().remove(this);
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