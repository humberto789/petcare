package com.imd.petcare.model.enums;

public enum SchedulingType {
    ERROR("error"),
    SUCCESS("success"),
    WARNING("warning");

    private final String description;

    SchedulingType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
