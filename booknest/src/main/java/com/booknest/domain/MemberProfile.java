package com.booknest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_profiles")
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

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

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "MemberProfile{id=%d}".formatted(id);
    }
}
