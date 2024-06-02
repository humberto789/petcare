package com.imd.petcare.repository;

import com.imd.petcare.model.BaseEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * A generic repository interface extending JpaRepository to provide additional methods for soft deletion.
 *
 * @param <T> the type of entities managed by this repository, extending BaseEntity
 *
 * This interface extends JpaRepository and provides additional methods for soft deletion of entities,
 * where entities are not physically removed from the database but marked as inactive.
 */
@NoRepositoryBean
public interface GenericRepository <T extends BaseEntity> extends JpaRepository<T, Long> {

    /**
     * Soft deletes an entity by ID, marking it as inactive.
     *
     * @param id the ID of the entity to delete
     *
     * This method retrieves the entity by its ID, sets its active flag to false,
     * and saves the updated entity to the database.
     */
    @Override
    @Transactional
    default void deleteById(Long id) {
        Optional<T> entity = findById(id);
        if (entity.isPresent()) {
            entity.get().setActive(false);
            save(entity.get());
        }
    }

    /**
     * Soft deletes an entity, marking it as inactive.
     *
     * @param obj the entity to delete
     *
     * This method sets the active flag of the given entity to false
     * and saves the updated entity to the database.
     */
    @Override
    @Transactional
    default void delete(T obj) {
        obj.setActive(false);
        save(obj);
    }

    /**
     * Soft deletes all entities in the given iterable, marking them as inactive.
     *
     * @param arg0 the iterable containing entities to delete
     *
     * This method iterates over the given iterable, soft deleting each entity
     * by calling deleteById() method.
     */
    @Override
    @Transactional
    default void deleteAll(Iterable<? extends T> arg0) {
        arg0.forEach(entity -> deleteById(entity.getId()));
    }
}
