package com.imd.petcare.repository;

import com.imd.petcare.model.RefreshToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing RefreshToken entities, extending GenericRepository.
 *
 * This repository interface provides methods for CRUD operations and additional queries specific to the RefreshToken entity.
 */
@Repository
public interface RefreshTokenRepository extends GenericRepository<RefreshToken>{

    /**
     * Retrieves a RefreshToken by its token value.
     *
     * @param token the token value to search for
     * @return an Optional containing the RefreshToken if found, otherwise empty
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Marks refresh tokens associated with a user as used.
     *
     * @param userId the ID of the user whose refresh tokens should be marked as used
     */
    @Modifying
    @Query("UPDATE RefreshToken SET isUsed = true WHERE user.id = :userId")
    void updateIsUsedByUserId(@Param("userId") Long userId);
}
