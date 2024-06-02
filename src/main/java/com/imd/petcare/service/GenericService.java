package com.imd.petcare.service;

import com.imd.petcare.mappers.DtoMapper;
import com.imd.petcare.model.BaseEntity;
import com.imd.petcare.repository.GenericRepository;
import com.imd.petcare.utils.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Generic service interface for managing entities and DTOs.
 *
 * @param <E>  the type of entities managed by this service, extending BaseEntity
 * @param <DTO> the type of DTOs managed by this service
 *
 * This generic service interface defines methods for managing entities and their corresponding DTOs.
 */
public interface GenericService<E extends BaseEntity, DTO> {
    /**
     * Gets the repository associated with the entity.
     *
     * @return The repository for the entity.
     */
    GenericRepository<E> getRepository();

    /**
     * Gets the DTO mapper associated with the entity.
     *
     * @return The DTO mapper for the entity.
     */
    DtoMapper<E, DTO> getDtoMapper();

    /**
     * Retrieves all entities using pagination.
     *
     * @param pageable Pagination information.
     * @return Page of DTOs representing the entities.
     */
    default Page<DTO> findAll(Pageable pageable) {
        Page<E> entityPage = getRepository().findAll(pageable);
        return new PageImpl<>(getDtoMapper().toDto(entityPage.getContent()), pageable, entityPage.getTotalElements());
    }

    /**
     * Retrieves an entity by its ID.
     *
     * @param id The ID of the entity.
     * @return The DTO representing the entity.
     * @throws ResourceNotFoundException if the ID is not found.
     */
    default DTO findById(Long id) {

        E entity = getRepository().findById(id).orElseThrow(() -> new ResourceNotFoundException("Id não encontrado: " + id));

        return getDtoMapper().toDto(entity);
    }

    /**
     * Creates a new entity based on the provided DTO.
     *
     * @param dto The DTO representing the entity to be created.
     * @return The DTO representing the created entity.
     */
    default DTO create(DTO dto) {
        E entity = getDtoMapper().toEntity(dto);
        validateBeforeSave(entity);
        return getDtoMapper().toDto(getRepository().save(entity));
    }

    /**
     * Updates an existing entity based on the provided DTO.
     *
     * @param id  The ID of the entity to be updated.
     * @param dto The DTO representing the updated entity.
     * @return The DTO representing the updated entity.
     */
    default DTO update(Long id, DTO dto) {

        getRepository().findById(id).orElseThrow(() -> new ResourceNotFoundException("Id não encontrado: " + id));

        E updatedEntity = getDtoMapper().toEntity(dto);
        updatedEntity.setId(id);
        validateBeforeUpdate(updatedEntity);
        getRepository().save(updatedEntity);

        return getDtoMapper().toDto(getRepository().save(updatedEntity));
    }

    /**
     * Deletes an entity by its ID.
     *
     * @param id The ID of the entity to be deleted.
     */
    default void deleteById(Long id) {
        E entity = getRepository().findById(id).orElseThrow(() -> new ResourceNotFoundException("Id não encontrado: " + id));
        entity.setActive(false);
        getRepository().save(entity);
    }

    default void validateBeforeSave(E entity) {
    }

    default void validateBeforeUpdate(E entity) {
    }
}
