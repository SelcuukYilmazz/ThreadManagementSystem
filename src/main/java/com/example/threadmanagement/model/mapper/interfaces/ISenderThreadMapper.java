package com.example.threadmanagement.model.mapper.interfaces;

import com.example.threadmanagement.model.dto.SenderThreadDto;
import com.example.threadmanagement.model.entity.SenderThreadEntity;
import org.mapstruct.Mapper;
import java.util.List;

/**
 * Mapper interface for converting between SenderThreadEntity and SenderThreadDto objects and List<objects>.
 * Uses Spring component model for dependency injection.
 */
@Mapper(componentModel = "spring")
public interface ISenderThreadMapper {

    /**
     * Converts a SenderThreadEntity to a SenderThreadDto.
     * @param entity the entity to convert
     * @return corresponding DTO with mapped values
     */
    SenderThreadDto toDto(SenderThreadEntity entity);

    /**
     * Converts a SenderThreadDto to a SenderThreadEntity.
     * @param dto the DTO to convert
     * @return corresponding entity with mapped values
     */
    SenderThreadEntity toEntity(SenderThreadDto dto);

    /**
     * Converts a list of SenderThreadEntity objects to a list of SenderThreadDto objects.
     * @param entityList list of entities to convert
     * @return list of corresponding DTOs with mapped values
     */
    List<SenderThreadDto> toDtoList(List<SenderThreadEntity> entityList);

    /**
     * Converts a list of SenderThreadDto objects to a list of SenderThreadEntity objects.
     * @param dtoList list of DTOs to convert
     * @return list of corresponding entities with mapped values
     */
    List<SenderThreadEntity> toEntityList(List<SenderThreadDto> dtoList);
}