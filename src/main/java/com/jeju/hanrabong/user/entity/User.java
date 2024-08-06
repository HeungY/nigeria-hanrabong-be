package com.jeju.hanrabong.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private int score;

    @Column(name = "created_at",nullable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime createdAt;
}
