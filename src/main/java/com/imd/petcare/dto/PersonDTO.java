package com.imd.petcare.dto;

import java.time.LocalDate;

public record PersonDTO(
        long id,
        String name,
        String identifier,
        String phoneNumber,
        LocalDate birthDate) {
}
