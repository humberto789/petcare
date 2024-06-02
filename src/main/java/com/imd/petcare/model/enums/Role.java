package com.imd.petcare.model.enums;

public enum Role {
    ADMIN("Administrador"),
    USER("Usuario"),
    GROOMERS("Tratadores"),
    DOCTOR("Médico"),
    RECEPTIONIST("Recepcionista");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
