package com.booknest.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_profiles")
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 500)
    @Column(name = "address", length = 500)
    private String address;

    @NotNull
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    protected MemberProfile() {
    }

    public MemberProfile(String address, LocalDateTime joinedAt) {
        this.address = address;
        this.joinedAt = joinedAt;
    }

    public Long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public Member getMember() {
        return member;
    }

    void assignMember(Member member) {
        this.member = member;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "MemberProfile{id=%d}".formatted(id);
    }
}