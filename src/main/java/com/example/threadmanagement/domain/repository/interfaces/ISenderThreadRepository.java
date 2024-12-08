package com.example.threadmanagement.domain.repository.interfaces;

import com.example.threadmanagement.model.entity.SenderThreadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ISenderThreadRepository extends JpaRepository<SenderThreadEntity, UUID> {
}