package com.example.hackAttemptService.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Password {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "password_value")
    private String passwordValue;

    @Column(name = "app_name")
    private String appName;

    @Column(name = "password_label")
    private String passwordLabel;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public Password(String passwordValue, User user) {
        this.passwordValue = passwordValue;
        this.user = user;
    }

    public Password(String passwordValue, String appName, String passwordLabel, User user) {
        this.passwordValue = passwordValue;
        this.appName = appName;
        this.passwordLabel = passwordLabel;
        this.user = user;
    }
}
