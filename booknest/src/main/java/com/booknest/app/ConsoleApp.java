package com.booknest.app;

import com.booknest.domain.Author;
import com.booknest.domain.Book;
import com.booknest.domain.Loan;
import com.booknest.domain.Member;
import com.booknest.exception.BookNotFoundException;
import com.booknest.exception.DataAccessException;
import com.booknest.exception.InsufficientCopiesException;
import com.booknest.exception.InvalidInputException;
import com.booknest.exception.LoanNotFoundException;
import com.booknest.exception.MemberNotFoundException;
import com.booknest.service.CatalogService;
import com.booknest.service.LendingService;
import com.booknest.service.MemberService;
import com.booknest.service.ReportService;
import com.booknest.util.JpaUtil;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class ConsoleApp {

    private final Scanner scanner = new Scanner(System.in);
    private final CatalogService catalogService = new CatalogService();
    private final MemberService memberService = new MemberService();
    private final LendingService lendingService = new LendingService();
    private final ReportService reportService = new ReportService();

    private static final int PAGE_SIZE = 5;

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createCategory();
                    case "2" -> listCategories();
                    case "3" -> createAuthor();
                    case "4" -> listAuthors();
                    case "5" -> createBook();
                    case "6" -> listBooks();
                    case "7" -> searchBooks();
                    case "8" -> registerMember();
                    case "9" -> listMembers();
                    case "10" -> searchMembers();
                    case "11" -> lendBooks();
                    case "12" -> returnLoan();
                    case "13" -> viewLoanDetail();
                    case "14" -> listBooksByCategory();
                    case "15" -> listBooksByAuthor();
                    case "16" -> advancedBookSearch();
                    case "17" -> activeLoansByEmail();
                    case "18" -> overdueLoans();
                    case "19" -> countLoansByStatus();
                    case "20" -> topBorrowedBooks();
                    case "0" -> running = false;
                    default -> System.out.println("Unknown option.");
                }
            } catch (InvalidInputException | BookNotFoundException | MemberNotFoundException
                     | InsufficientCopiesException | LoanNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (DataAccessException e) {
                System.out.println("Unexpected data access error: " + e.getMessage());
            } catch (RuntimeException e) {
                // catches bad numeric input (NumberFormatException) etc. without crashing the app
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
        JpaUtil.close();
        System.out.println("Bye.");
    }

    private void printMenu() {
        System.out.println("""

                ==== BookNest ====
                 1) Create category            11) Lend book(s) to a member
                 2) List categories            12) Return a loan
                 3) Create author              13) View loan detail
                 4) List authors               14) List books by category
                 5) Create book                15) List books by author
                 6) List books (paged)         16) Advanced book search (keyword/category/author/availability)
                 7) Search books by title      17) Active loans by member email
                 8) Register member            18) Overdue loans
                 9) List members               19) Count loans by status
                10) Search members by name     20) Top borrowed books
                                                 0) Exit
                Choose:""");
    }

    // --- Category ---

    private void createCategory() {
        System.out.print("Category name: ");
        var category = catalogService.createCategory(scanner.nextLine().trim());
        System.out.println("Created: " + category);
    }

    private void listCategories() {
        catalogService.listCategories().forEach(System.out::println);
    }

    // --- Author ---

    private void createAuthor() {
        System.out.print("Author full name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Bio (optional): ");
        String bio = scanner.nextLine().trim();
        Author author = catalogService.createAuthor(name, bio.isBlank() ? null : bio);
        System.out.println("Created: " + author);
    }

    private void listAuthors() {
        catalogService.listAuthors().forEach(System.out::println);
    }

    // --- Book ---

    private void createBook() {
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Published year: ");
        Integer year = parseIntOrNull(scanner.nextLine().trim());
        System.out.print("Available copies: ");
        Integer copies = parseIntOrNull(scanner.nextLine().trim());
        System.out.print("Category id (blank = none): ");
        String catIn = scanner.nextLine().trim();
        Long categoryId = catIn.isBlank() ? null : Long.valueOf(catIn);
        System.out.print("Author ids, comma separated (blank = none): ");
        String authIn = scanner.nextLine().trim();
        List<Long> authorIds = authIn.isBlank()
                ? List.of()
                : Arrays.stream(authIn.split(",")).map(String::trim).map(Long::valueOf).toList();

        Book book = catalogService.createBook(isbn, title, year, copies, categoryId, authorIds);
        System.out.println("Created: " + book);
    }

    private void listBooks() {
        System.out.print("Page number (starting at 0): ");
        int page = Integer.parseInt(scanner.nextLine().trim());
        catalogService.listBooksPaged(page, PAGE_SIZE).forEach(System.out::println);
    }

    private void searchBooks() {
        System.out.print("Keyword: ");
        catalogService.searchBooksByTitle(scanner.nextLine().trim()).forEach(System.out::println);
    }

    private void listBooksByCategory() {
        System.out.print("Category id: ");
        Long categoryId = Long.valueOf(scanner.nextLine().trim());
        catalogService.listBooksByCategory(categoryId).forEach(System.out::println);
    }

    private void listBooksByAuthor() {
        System.out.print("Author id: ");
        Long authorId = Long.valueOf(scanner.nextLine().trim());
        catalogService.listBooksByAuthor(authorId).forEach(System.out::println);
    }

    private void advancedBookSearch() {
        System.out.print("Keyword (blank = any): ");
        String keyword = blankToNull(scanner.nextLine().trim());
        System.out.print("Category id (blank = any): ");
        Long categoryId = blankToNullLong(scanner.nextLine().trim());
        System.out.print("Author id (blank = any): ");
        Long authorId = blankToNullLong(scanner.nextLine().trim());
        System.out.print("Only available copies > 0? (y/N): ");
        Boolean onlyAvailable = scanner.nextLine().trim().equalsIgnoreCase("y");
        System.out.print("Page number (starting at 0): ");
        int page = Integer.parseInt(scanner.nextLine().trim());

        catalogService.searchBooksDynamic(keyword, categoryId, authorId, onlyAvailable, page, PAGE_SIZE)
                .forEach(System.out::println);
    }

    // --- Member ---

    private void registerMember() {
        System.out.print("Full name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        Member member = memberService.register(name, email, phone);
        System.out.println("Registered: " + member);
    }

    private void listMembers() {
        memberService.listMembers().forEach(System.out::println);
    }

    private void searchMembers() {
        System.out.print("Keyword: ");
        memberService.searchByName(scanner.nextLine().trim()).forEach(System.out::println);
    }

    // --- Lending ---

    private void lendBooks() {
        System.out.print("Member id: ");
        Long memberId = Long.valueOf(scanner.nextLine().trim());

        Map<Long, Integer> items = new LinkedHashMap<>();
        System.out.println("Enter book id / quantity pairs. Leave book id blank to stop.");
        while (true) {
            System.out.print("Book id: ");
            String bookIdIn = scanner.nextLine().trim();
            if (bookIdIn.isBlank()) {
                break;
            }
            System.out.print("Quantity: ");
            items.put(Long.valueOf(bookIdIn), parseIntOrNull(scanner.nextLine().trim()));
        }

        Loan loan = lendingService.lendBooks(memberId, items);
        System.out.println("Loan created: " + loan + " (due " + loan.getDueDate() + ")");
    }

    private void returnLoan() {
        System.out.print("Loan id: ");
        Long loanId = Long.valueOf(scanner.nextLine().trim());
        Loan loan = lendingService.returnLoan(loanId);
        System.out.println("Returned: " + loan);
    }

    private void viewLoanDetail() {
        System.out.print("Loan id: ");
        Long loanId = Long.valueOf(scanner.nextLine().trim());
        Loan loan = lendingService.viewLoanDetail(loanId);
        System.out.println(loan + ", member=" + loan.getMember().getFullName());
        loan.getItems().forEach(item ->
                System.out.println("  - " + item.getQuantity() + " x " + item.getBook().getTitle()));
    }

    // --- Reports ---

    private void activeLoansByEmail() {
        System.out.print("Member email: ");
        String email = scanner.nextLine().trim();
        List<Loan> loans = reportService.activeLoansByMemberEmail(email);
        if (loans.isEmpty()) {
            System.out.println("No active loans for " + email);
        }
        loans.forEach(System.out::println);
    }

    private void overdueLoans() {
        List<Loan> loans = reportService.overdueLoans();
        if (loans.isEmpty()) {
            System.out.println("No overdue loans.");
        }
        loans.forEach(System.out::println);
    }

    private void countLoansByStatus() {
        reportService.countLoansByStatus()
                .forEach(row -> System.out.println(row.status() + ": " + row.count()));
    }

    private void topBorrowedBooks() {
        System.out.print("How many to show: ");
        int limit = Integer.parseInt(scanner.nextLine().trim());
        reportService.topBorrowedBooks(limit)
                .forEach(row -> System.out.println(row.title() + " -> " + row.totalQuantityBorrowed()));
    }

    private Integer parseIntOrNull(String s) {
        return s.isBlank() ? null : Integer.valueOf(s);
    }

    private String blankToNull(String s) {
        return s.isBlank() ? null : s;
    }

    private Long blankToNullLong(String s) {
        return s.isBlank() ? null : Long.valueOf(s);
    }
}