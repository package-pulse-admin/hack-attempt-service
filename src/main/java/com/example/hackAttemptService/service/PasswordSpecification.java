package com.example.hackAttemptService.service;

import com.example.hackAttemptService.model.Password;
import com.example.hackAttemptService.model.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class PasswordSpecification {

    public static Specification<Password> belongsToUser(User user) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
    }

    public static Specification<Password> hasAppName(String appName) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(appName)) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("appName")), "%" + appName.toLowerCase() + "%");
            }
            return null;
        };
    }

    public static Specification<Password> hasPasswordLabel(String passwordLabel) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(passwordLabel)) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("passwordLabel")), "%" + passwordLabel.toLowerCase() + "%");
            }
            return null;
        };
    }
}
