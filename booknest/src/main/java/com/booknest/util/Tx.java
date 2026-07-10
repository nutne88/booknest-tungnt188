package com.booknest.util;

import com.booknest.exception.DataAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public final class Tx {

    private Tx() {
    }

    @FunctionalInterface
    public interface Work<T> {
        T apply(EntityManager em) throws Exception;
    }

    public static <T> T run(Work<T> work) {
        EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T result = work.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            rollbackQuietly(tx);
            throw e;
        } catch (Exception e) {
            rollbackQuietly(tx);
            throw new DataAccessException("Unexpected persistence error", e);
        } finally {
            em.close();
        }
    }

    private static void rollbackQuietly(EntityTransaction tx) {
        if (tx.isActive()) {
            tx.rollback();
        }
    }
}