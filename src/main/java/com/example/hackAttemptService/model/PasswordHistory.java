package com.example.hackAttemptService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class PasswordHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "password_id")
    private Password password;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "password_value")
    private String passwordValue;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PasswordHistory(Password password, User user, String passwordValue, LocalDateTime createdAt) {
        this.password = password;
        this.user = user;
        this.passwordValue = passwordValue;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

}
