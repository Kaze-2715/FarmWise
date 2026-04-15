package com.agri.platform.DTO.user;

public record UserUpdateDTO(
        String userId,
        String username,
        String phoneNumber,
        String email
        ) {
}
