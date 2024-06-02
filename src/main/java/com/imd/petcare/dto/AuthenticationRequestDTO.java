package com.imd.petcare.dto;

public record AuthenticationRequestDTO(
        String login,
        String password
) {
}
