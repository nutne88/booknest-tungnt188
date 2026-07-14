package com.booknest.service;

import com.booknest.domain.Author;
import com.booknest.domain.Book;
import com.booknest.domain.Category;
import com.booknest.exception.InvalidInputException;
import com.booknest.repository.AuthorRepository;
import com.booknest.repository.BookRepository;
import com.booknest.repository.CategoryRepository;
import com.booknest.util.Tx;
import com.booknest.util.ValidationUtil;

import java.util.List;

public class CatalogService {

    public Category createCategory(String name) {
        return Tx.run(em -> {
            Category category = new Category(name);
            validateOrThrow(category);
            return new CategoryRepository(em).save(category);
        });
    }

    public List<Category> listCategories() {
        return Tx.run(em -> new CategoryRepository(em).findAll());
    }

    public List<Category> searchCategories(String keyword) {
        return Tx.run(em -> new CategoryRepository(em).searchByNameKeyword(keyword));
    }

    public Author createAuthor(String fullName, String bio) {
        return Tx.run(em -> {
            Author author = new Author(fullName, bio);
            validateOrThrow(author);
            return new AuthorRepository(em).save(author);
        });
    }

    public List<Author> listAuthors() {
        return Tx.run(em -> new AuthorRepository(em).findAll());
    }

    public List<Author> searchAuthors(String keyword) {
        return Tx.run(em -> new AuthorRepository(em).searchByNameKeyword(keyword));
    }

    public Book createBook(String isbn, String title, Integer publishedYear, Integer availableCopies,
                           Long categoryId, List<Long> authorIds) {
        return Tx.run(em -> {
            BookRepository bookRepository = new BookRepository(em);

            if (bookRepository.findByIsbn(isbn).isPresent()) {
                throw new InvalidInputException("A book with ISBN " + isbn + " already exists");
            }

            Book book = new Book(isbn, title, publishedYear, availableCopies);
            validateOrThrow(book);

            if (categoryId != null) {
                Category category = em.find(Category.class, categoryId);
                if (category == null) {
                    throw new InvalidInputException("Category id " + categoryId + " does not exist");
                }
                book.assignCategory(category);
            }

            if (authorIds != null) {
                for (Long authorId : authorIds) {
                    Author author = em.find(Author.class, authorId);
                    if (author == null) {
                        throw new InvalidInputException("Author id " + authorId + " does not exist");
                    }
                    book.addAuthor(author);
                }
            }

            return bookRepository.save(book);
        });
    }

    public List<Book> listBooks() {
        return Tx.run(em -> new BookRepository(em).findAll());
    }

    public List<Book> listBooksPaged(int pageIndex, int pageSize) {
        return Tx.run(em -> new BookRepository(em).findAllPaged(pageIndex, pageSize));
    }

    public List<Book> searchBooksByTitlePaged(String keyword, int pageIndex, int pageSize) {
        return Tx.run(em -> new BookRepository(em).searchByTitleKeywordPaged(keyword, pageIndex, pageSize));
    }

    public List<Book> listBooksByCategory(Long categoryId) {
        return Tx.run(em -> new BookRepository(em).findByCategory(categoryId));
    }

    public List<Book> listBooksByAuthor(Long authorId) {
        return Tx.run(em -> new BookRepository(em).findByAuthor(authorId));
    }

    public List<Book> searchBooksDynamic(String keyword, Long categoryId, Long authorId,
                                         Boolean onlyAvailable, int pageIndex, int pageSize) {
        return Tx.run(em -> new BookRepository(em)
                .searchDynamic(keyword, categoryId, authorId, onlyAvailable, pageIndex, pageSize));
    }

    private static <T> void validateOrThrow(T entity) {
        var violations = ValidationUtil.validate(entity);
        if (!violations.isEmpty()) {
            throw new InvalidInputException(String.join("; ", violations));
        }
    }
}