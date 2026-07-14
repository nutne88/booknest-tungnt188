package com.booknest.repository;

import com.booknest.domain.Member;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class MemberRepository {

    private final EntityManager em;

    public MemberRepository(EntityManager em) {
        this.em = em;
    }

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public Optional<Member> findByEmail(String email) {
        List<Member> results = em.createQuery(
                        "select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m order by m.fullName", Member.class)
                .getResultList();
    }

    public List<Member> searchByNameKeyword(String keyword) {
        return em.createQuery(
                        "select m from Member m where lower(m.fullName) like lower(:kw) order by m.fullName",
                        Member.class)
                .setParameter("kw", "%" + keyword + "%")
                .getResultList();
    }

    public Optional<Member> findWithProfileById(Long id) {
        List<Member> results = em.createQuery(
                        "select m from Member m left join fetch m.profile where m.id = :id",
                        Member.class)
                .setParameter("id", id)
                .getResultList();
        return results.stream().findFirst();
    }
}