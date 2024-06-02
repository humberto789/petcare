package com.imd.petcare.mappers;

import java.util.List;

/**
 * Interface for mapping between entity objects and DTOs (Data Transfer Objects).
 *
 * @param <E>   the type of entity object
 * @param <DTO> the type of DTO (Data Transfer Object)
 */
public interface DtoMapper<E, DTO> {

    /**
     * Converts an entity to its corresponding DTO.
     *
     * @param entity The entity to be converted.
     * @return The DTO representing the entity.
     */
    DTO toDto(E entity);

    /**
     * Converts a list of entities to a list of DTOs.
     *
     * @param entity The list of entities to be converted.
     * @return The list of DTOs representing the entities.
     */
    default List<DTO> toDto(List<E> entity) {
        return entity.stream().map(this::toDto).toList();
    }

    /**
     * Converts a DTO to its corresponding entity.
     *
     * @param dto The DTO to be converted.
     * @return The entity representing the DTO.
     */
    E toEntity(DTO dto);
}
