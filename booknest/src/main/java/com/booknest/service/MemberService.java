package com.booknest.service;

import com.booknest.domain.Member;
import com.booknest.exception.InvalidInputException;
import com.booknest.repository.MemberRepository;
import com.booknest.util.Tx;
import com.booknest.util.ValidationUtil;

import java.util.List;

public class MemberService {

    public Member register(String fullName, String email, String phone) {
        return Tx.run(em -> {
            Member member = new Member(fullName, email, phone);

            var violations = ValidationUtil.validate(member);
            if (!violations.isEmpty()) {
                throw new InvalidInputException(String.join("; ", violations));
            }

            MemberRepository repository = new MemberRepository(em);
            if (repository.findByEmail(email).isPresent()) {
                throw new InvalidInputException("A member with email " + email + " already exists");
            }

            return repository.save(member);
        });
    }

    public List<Member> listMembers() {
        return Tx.run(em -> new MemberRepository(em).findAll());
    }

    public List<Member> searchByName(String keyword) {
        return Tx.run(em -> new MemberRepository(em).searchByNameKeyword(keyword));
    }
}