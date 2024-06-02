package com.imd.petcare.controller;

import com.imd.petcare.dto.*;
import com.imd.petcare.model.User;
import com.imd.petcare.service.UserService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing user entities.
 *
 * This controller provides endpoints for CRUD operations and other user-related functionalities.
 *
 */
@RestController
@RequestMapping("/v1/users")
@Validated
public class UserController extends GenericController<User, UserDTO, UserService> {
    protected UserController(UserService service) {
        super(service);
    }

    /**
     * Get all the users.
     * Only users with the Admin role have access to this endpoint.
     *
     * @return ResponseEntity containing the list of DTOs and status 200 (OK).
     */
    @Override
    @GetMapping
    public ResponseEntity<ApiResponseDTO<PageImpl<EntityDTO>>> getAll(@ParameterObject Pageable pageable) {
        return super.getAll(pageable);
    }

    /**
     * Get a user by its ID.
     * Users with the Admin role have total access to this endpoint,
     * users with Admin Seller role have access only to users from their own seller store,
     * and users with Seller role have access only for their own user.
     *
     * @param id The ID of the user to get.
     * @return ResponseEntity containing the DTO and status 200 (OK).
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EntityDTO>> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    /**
     * Update an existing user.
     * Users with the Admin role have total access to this endpoint,
     * users with Admin Seller role have access only to users from their own seller store,
     * and users with Seller role have access only for their own user.
     *
     * @param id  The ID of the user to update.
     * @param dto The DTO representing the updated user.
     * @return ResponseEntity containing the DTO and status 200 (OK).
     */
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EntityDTO>> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        return super.update(id, dto);
    }

    /**
     * Save a new user.
     * Only users with the Admin role have access to this endpoint.
     *
     * @param dto The DTO representing the user to save.
     * @return ResponseEntity containing the DTO and status 201 (CREATED).
     */
    @Override
    @PostMapping
    public ResponseEntity<ApiResponseDTO<EntityDTO>> create(@Valid @RequestBody UserDTO dto) {
        return super.create(dto);
    }

    /**
     * Retrieves all available roles.
     * Only users with the Admin role have access to this endpoint.
     *
     * @return ResponseEntity containing the list of RoleDTO and status 200 (OK).
     */
    @GetMapping("/roles")
    public ResponseEntity<ApiResponseDTO<List<RoleDTO>>> getAllRoles() {
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Todas as funções disponíveis.", service.getAllRoles(), null));
    }

    /**
     * Checks if an email exists in the system.
     * Only users with the Admin and Admin Seller role have access to this endpoint.
     *
     * @param email The email to check.
     * @return ResponseEntity with ApiResponseDTO containing a Boolean indicating whether the email exists.
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponseDTO<Boolean>> checkEmailExists(@RequestParam String email) {

        Boolean exists = service.existsByEmail(email);

        return ResponseEntity.ok(new ApiResponseDTO<>(
                true,
                Boolean.TRUE.equals(exists) ? "Sucesso: o email existe."
                        : "Sucesso: o email não existe.",
                exists,
                null
        ));
    }

    /**
     * Deletes a user by ID.
     * Only users with the Admin role have access to this endpoint.
     *
     * @param id The ID of the user to delete.
     * @return ResponseEntity with ApiResponseDTO indicating the success of the operation.
     */
    @Override
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponseDTO<UserDTO>> delete(@PathVariable Long id) {

        service.deleteById(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(
                true,
                "Sucesso: Usuário removido.",
                null,
                null
        ));
    }

    /**
     * Retrieves a user's details by ID.
     * Users with the Admin role have total access to this endpoint,
     * meanwhile Admin Sellers have access only to Users from its own seller store.
     *
     * @param id The ID of the user.
     * @return ResponseEntity with ApiResponseDTO containing the UserDetailsDTO.
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<ApiResponseDTO<UserDetailsDTO>> getUsersDetails(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(
                new ApiResponseDTO<>(
                        true,
                        "Sucesso: Usuário retornado com sucesso.",
                        service.findByUserId(id),
                        null
                ));
    }
}
