package com.booknest.app;

import com.booknest.domain.Book;
import com.booknest.domain.Loan;
import com.booknest.repository.LoanRepository;
import com.booknest.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public final class PerformanceDemo {

    private PerformanceDemo() {
    }
    public static void runNPlusOneDemo() {
        EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            System.out.println("\n--- N+1 DEMO (no fetch join) --- read the SQL log below ---");

            List<Loan> loans = new LoanRepository(em).findAllNaive();
            System.out.println("Loaded " + loans.size() + " loan(s) with 1 query.");
            System.out.println("Now touching lazy member + items/book for each loan "
                    + "(watch for extra SELECT/batched IN queries above):");

            for (Loan loan : loans) {
                String memberName = loan.getMember().getFullName(); // may trigger a lazy load
                int itemCount = loan.getItems().size();              // may trigger a lazy load
                for (var item : loan.getItems()) {
                    item.getBook().getTitle();                       // may trigger a lazy load
                }
                System.out.println("  Loan " + loan.getId() + " -> member=" + memberName + ", items=" + itemCount);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public static void runOptimizedFetchDemo() {
        EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            System.out.println("\n--- OPTIMIZED DEMO (fetch join) --- should be exactly ONE query ---");

            List<Loan> loans = new LoanRepository(em).findAllWithDetails();
            for (Loan loan : loans) {
                String memberName = loan.getMember().getFullName(); // already loaded, no extra query
                System.out.println("  Loan " + loan.getId() + " -> member=" + memberName
                        + ", items=" + loan.getItems().size());
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public static void runFirstLevelCacheDemo(Long bookId) {
        EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            System.out.println("\n--- FIRST-LEVEL CACHE DEMO --- should be exactly ONE select ---");

            Book first = em.find(Book.class, bookId);
            if (first == null) {
                System.out.println("No book with id " + bookId + " — create one first.");
                em.getTransaction().commit();
                return;
            }
            Book second = em.find(Book.class, bookId); // no new SELECT: served from persistence context

            System.out.println("first  = " + first);
            System.out.println("second = " + second);
            System.out.println("first == second (same managed instance)? " + (first == second));

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}