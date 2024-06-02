package com.imd.petcare.dto;

public record AuthenticationResponseDTO(
        String token,
        String refreshToken
) {
}