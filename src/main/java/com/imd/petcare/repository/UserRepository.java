package com.imd.petcare.repository;

import com.imd.petcare.model.User;
import com.imd.petcare.model.enums.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing User entities, extending GenericRepository.
 *
 * This repository interface provides methods for CRUD operations and additional queries specific to the User entity.
 */
@Repository
public interface UserRepository extends GenericRepository<User> {

    /**
     * Checks if a user with the given login exists.
     *
     * @param login the login to check for existence
     * @return true if a user with the given login exists, false otherwise
     */
    boolean existsByLogin(String login);

    /**
     * Checks if a user with the given email exists.
     *
     * @param email the email to check for existence
     * @return true if a user with the given email exists, false otherwise
     */
    @Query("select (count(u) > 0) from User u where u.email = ?1")
    boolean existsPersonByEmail(String email);

    /**
     * Checks if a user with the given identifier exists.
     *
     * @param identifier the identifier to check for existence
     * @return true if a user with the given identifier exists, false otherwise
     */
    @Query("select (count(u) > 0) from User u where u.person.identifier = ?1")
    boolean existsPersonByIdentifier(String identifier);

    /**
     * Retrieves a user by their login.
     *
     * @param login the login of the user to retrieve
     * @return an Optional containing the user if found, otherwise empty
     */
    @Query("select u from User u where u.login = ?1")
    Optional<User> findByLogin(String login);

    /**
     * Retrieves a user by their email.
     *
     * @param login the email of the user to retrieve
     * @return an Optional containing the user if found, otherwise empty
     */
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> findByEmail(String login);

    /**
     * Updates the role of a user by their ID.
     *
     * @param role the new role to set
     * @param id   the ID of the user to update
     */
    @Transactional
    default void updateRoleById(Role role, Long id){
        Optional<User> user = findById(id);
        if(user.isPresent()){
            user.get().setRole(role);
        }
    }
}