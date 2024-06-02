package com.imd.petcare.repository;

import com.imd.petcare.model.RecoveryToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing RecoveryToken entities, extending GenericRepository.
 *
 * This repository interface provides methods for CRUD operations and additional queries specific to the RecoveryToken entity.
 */
@Repository
public interface RecoveryTokenRepository extends GenericRepository<RecoveryToken> {

    /**
     * Retrieves a RecoveryToken by its token value.
     *
     * @param token the token value to search for
     * @return an Optional containing the RecoveryToken if found, otherwise empty
     */
    Optional<RecoveryToken> findByToken(String token);

    /**
     * Marks recovery tokens associated with a user as used.
     *
     * @param userId the ID of the user whose recovery tokens should be marked as used
     * @return the number of recovery tokens updated
     */
    @Modifying
    @Query("UPDATE RecoveryToken rt SET rt.isUsed = true WHERE rt.user.id = :userId")
    int updateIsUsedByUserId(@Param("userId") Long userId);
}
