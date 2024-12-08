package com.example.threadmanagement.domain.repository.interfaces;

import com.example.threadmanagement.model.entity.ReceiverThreadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * Repository interface for managing ReceiverThreadEntity objects.
 * - This interface provides methods to interact with the database for `ReceiverThreadEntity` objects.
 * - Inherits basic CRUD and query capabilities from JpaRepository.
 */
@Repository // Marks this as a Spring-managed repository bean.
public interface IReceiverThreadRepository extends JpaRepository<ReceiverThreadEntity, UUID> {

    // No custom methods are defined here for now.
    // JpaRepository provides out-of-the-box methods such as:
    // - save() for saving an entity.
    // - findById() for finding an entity by its primary key (UUID in this case).
    // - findAll() for retrieving all entities.
    // - deleteById() for deleting an entity by its primary key.
    // Additional custom methods can be added here if needed, using Spring Data JPA query derivation.
}
