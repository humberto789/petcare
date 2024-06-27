package com.imd.petcare.dto;

import com.imd.petcare.model.enums.SchedulingType;

public record SchedulingDTO(
        Long id,
        Long userId,
        String title,
        String description,
        Long month,
        Long day,
        Long year,
        SchedulingType type
) implements EntityDTO {
    @Override
    public EntityDTO toResponse() {
        return new SchedulingDTO(this.id(), this.userId(), this.title(), this.description, this.month, this.day, this.year, this.type);
    }
}
