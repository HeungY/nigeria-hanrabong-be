package com.jeju.hanrabong.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
    @ColumnDefault("100")
    private int score;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int count;

    @Column(name = "created_at",nullable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (score == 0) {
            score = 100;
        }
    }
}
