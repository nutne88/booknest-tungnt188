package com.booknest.app;

import com.booknest.domain.Author;
import com.booknest.domain.Book;
import com.booknest.domain.Category;
import com.booknest.domain.Loan;
import com.booknest.domain.Member;
import com.booknest.exception.InvalidInputException;
import com.booknest.service.CatalogService;
import com.booknest.service.LendingService;
import com.booknest.service.MemberService;

import java.util.List;
import java.util.Map;

public final class SeedData {

    private SeedData() {
    }

    public static void run() {
        CatalogService catalogService = new CatalogService();
        MemberService memberService = new MemberService();
        LendingService lendingService = new LendingService();

        System.out.println("\n--- Seeding sample data ---");

        try {
            Category fiction = catalogService.createCategory("Fiction");
            Category science = catalogService.createCategory("Science");
            Category history = catalogService.createCategory("History");

            Author orwell = catalogService.createAuthor("George Orwell",
                    "English novelist and essayist, author of 1984 and Animal Farm.");
            Author asimov = catalogService.createAuthor("Isaac Asimov",
                    "American writer and biochemist, known for science fiction works.");
            Author harari = catalogService.createAuthor("Yuval Noah Harari",
                    "Israeli historian, author of Sapiens and Homo Deus.");

            Book book1984 = catalogService.createBook(
                    "978-0451524935", "1984", 1949, 3,
                    fiction.getId(), List.of(orwell.getId()));
            Book foundation = catalogService.createBook(
                    "978-0553293357", "Foundation", 1951, 2,
                    science.getId(), List.of(asimov.getId()));
            Book sapiens = catalogService.createBook(
                    "978-0062316097", "Sapiens: A Brief History of Humankind", 2011, 4,
                    history.getId(), List.of(harari.getId()));

            Member memberA = memberService.register("Nguyen Van A", "a.nguyen@example.com",
                    "0900000001", "12 Le Loi, Hai Phong");
            Member memberB = memberService.register("Tran Thi B", "b.tran@example.com",
                    "0900000002", "45 Tran Phu, Hai Phong");

            Loan sampleLoan = lendingService.lendBooks(memberA.getId(), Map.of(book1984.getId(), 1));

            System.out.println("Seeded: 3 categories, 3 authors, 3 books, 2 members (each with a MemberProfile).");
            System.out.println("Seeded 1 sample loan: " + sampleLoan
                    + " for " + memberA.getFullName() + " (book: " + book1984.getTitle() + ")");
            System.out.println("Member B (" + memberB.getEmail() + ") registered with no active loans yet.");
            System.out.println("Try: option 25 (view member detail) with member id " + memberA.getId()
                    + " to see the joined MemberProfile.");
            System.out.println("Try: option 23 (first-level cache demo) with book id " + foundation.getId()
                    + ", or option 20 (top borrowed books) after lending more copies.");

        } catch (InvalidInputException e) {
            System.out.println("Seed skipped some records (likely already seeded): " + e.getMessage());
        }
    }
}