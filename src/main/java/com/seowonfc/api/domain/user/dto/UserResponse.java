package com.seowonfc.api.domain.user.dto;

import com.seowonfc.api.domain.user.Role;
import com.seowonfc.api.domain.user.User;

public record UserResponse(Long id, String name, String email, Role role) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}