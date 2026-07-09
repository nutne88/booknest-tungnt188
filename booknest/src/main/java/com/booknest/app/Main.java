package com.booknest.app;

import com.booknest.domain.*;
import com.booknest.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Category fiction = new Category("Fiction");
            Author orwell = new Author("George Orwell", "English novelist and essayist.");
            Book book = new Book("978-0451524935", "1984", 1949, 3);
            Member member = new Member("Nguyen Van A", "a.nguyen@example.com", "0900000000");
            MemberProfile profile = new MemberProfile("Hanoi, Vietnam", LocalDateTime.now());

            em.persist(fiction);
            em.persist(orwell);
            em.persist(book);
            em.persist(member);
            em.persist(profile);

            tx.commit();

            System.out.println("Persisted successfully:");
            System.out.println(" " + fiction);
            System.out.println(" " + orwell);
            System.out.println(" " + book);
            System.out.println(" " + member);
            System.out.println(" " + profile);

        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
            JpaUtil.close();
        }
    }
}
