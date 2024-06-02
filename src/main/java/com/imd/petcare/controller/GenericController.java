package com.imd.petcare.controller;

import com.imd.petcare.dto.ApiResponseDTO;
import com.imd.petcare.dto.EntityDTO;
import com.imd.petcare.model.BaseEntity;
import com.imd.petcare.service.GenericService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Abstract controller class for managing generic entities.
 *
 * This abstract controller provides basic CRUD endpoints and other operations for managing entities.
 *
 * @param <E>  the type of entities managed by this controller, extending BaseEntity
 * @param <DTO> the type of DTOs used for entities, extending EntityDTO
 * @param <S>   the service class responsible for entity-related operations
 */
@Validated
public abstract class GenericController<E extends BaseEntity, DTO extends EntityDTO, S extends GenericService<E, DTO>> {

    protected S service;

    protected GenericController(S service) {
        this.service = service;
    }

    /**
     * Retrieves a paginated list of entities.
     *
     * @param pageable the pagination and sorting information
     * @return a ResponseEntity containing an ApiResponseDTO with a PageImpl of EntityDTOs
     *
     * This method handles GET requests to retrieve all entities, returning a paginated response
     * based on the provided Pageable parameter.
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<PageImpl<EntityDTO>>> getAll(@ParameterObject Pageable pageable) {
        var page = service.findAll(pageable);
        var res = new PageImpl<>(page.getContent().stream().map(DTO::toResponse).toList(), pageable,
                page.getTotalElements());

        return ResponseEntity.ok(new ApiResponseDTO<>(
                true,
                "Sucesso: Entidades localizadas com sucesso.",
                res,
                null));
    }

    /**
     * Retrieves an entity by its ID.
     *
     * @param id the ID of the entity to retrieve
     * @return a ResponseEntity containing an ApiResponseDTO with the retrieved EntityDTO
     *
     * This method handles GET requests to retrieve a specific entity by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EntityDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponseDTO<>(
                true,
                "Sucesso: Entidade localizada com sucesso.",
                service.findById(id).toResponse(),
                null));
    }

    /**
     * Creates a new entity.
     *
     * @param dto the DTO containing the details of the entity to create
     * @return a ResponseEntity containing an ApiResponseDTO with the created EntityDTO
     *
     * This method handles POST requests to create a new entity and returns the created entity with a status of CREATED.
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<EntityDTO>> create(@Valid @RequestBody DTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO<>(
                true,
                "Sucesso: Entidade criada com sucesso.",
                service.create(dto).toResponse(),
                null));
    }

    /**
     * Updates an existing entity by its ID.
     *
     * @param id  the ID of the entity to update
     * @param dto the DTO containing the updated details of the entity
     * @return a ResponseEntity containing an ApiResponseDTO with the updated EntityDTO
     *
     * This method handles PUT requests to update an existing entity and returns the updated entity with a status of CREATED.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EntityDTO>> update(@PathVariable Long id, @Valid @RequestBody DTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO<>(
                true,
                "Sucesso: A entidade foi atualizada com sucesso.",
                service.update(id, dto).toResponse(),
                null));
    }

    /**
     * Deletes an entity by its ID.
     *
     * @param id the ID of the entity to delete
     * @return a ResponseEntity containing an ApiResponseDTO indicating the deletion status
     *
     * This method handles DELETE requests to remove an entity by its ID.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponseDTO<DTO>> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(
                true,
                "Sucesso: A entidade foi removida com sucesso.",
                null,
                null));
    }
}
