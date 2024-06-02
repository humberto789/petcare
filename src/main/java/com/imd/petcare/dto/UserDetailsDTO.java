package com.imd.petcare.dto;

import com.imd.petcare.model.enums.Role;

import java.io.Serializable;
import java.time.LocalDate;

public record UserDetailsDTO (
        Long id,
        String name,
        String cpf,
        String email,
        String phone,
        LocalDate birthDate,
        Role role,
        String login) implements Serializable {}