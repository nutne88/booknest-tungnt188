package com.booknest.repository;

import com.booknest.domain.Category;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class CategoryRepository {

    private final EntityManager em;

    public CategoryRepository(EntityManager em) {
        this.em = em;
    }

    public Category save(Category category) {
        em.persist(category);
        return category;
    }

    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(em.find(Category.class, id));
    }

    public List<Category> findAll() {
        return em.createQuery("select c from Category c order by c.name", Category.class)
                .getResultList();
    }

    public List<Category> searchByNameKeyword(String keyword) {
        return em.createQuery(
                        "select c from Category c where lower(c.name) like lower(:kw) order by c.name",
                        Category.class)
                .setParameter("kw", "%" + keyword + "%")
                .getResultList();
    }
}