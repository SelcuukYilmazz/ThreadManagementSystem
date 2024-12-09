package com.example.threadmanagement.model.mapper.interfaces;

import com.example.threadmanagement.model.dto.ReceiverThreadDto;
import com.example.threadmanagement.model.entity.ReceiverThreadEntity;
import org.mapstruct.Mapper;
import java.util.List;

/**
 * Mapper interface for converting between ReceiverThreadEntity and ReceiverThreadDto objects and List<objects>
 * Uses Spring component model for dependency injection.
 */
@Mapper(componentModel = "spring")
public interface IReceiverThreadMapper {

    /**
     * Converts a ReceiverThreadEntity to a ReceiverThreadDto.
     * @param entity the entity to convert
     * @return corresponding DTO with mapped values
     */
    ReceiverThreadDto toDto(ReceiverThreadEntity entity);

    /**
     * Converts a ReceiverThreadDto to a ReceiverThreadEntity.
     * @param dto the DTO to convert
     * @return corresponding entity with mapped values
     */

    ReceiverThreadEntity toEntity(ReceiverThreadDto dto);

    /**
     * Converts a list of ReceiverThreadEntity objects to a list of ReceiverThreadDto objects.
     * @param entityList list of entities to convert
     * @return list of corresponding DTOs with mapped values
     */
    List<ReceiverThreadDto> toDtoList(List<ReceiverThreadEntity> entityList);

    /**
     * Converts a list of ReceiverThreadDto objects to a list of ReceiverThreadEntity objects.
     * @param dtoList list of DTOs to convert
     * @return list of corresponding entities with mapped values
     */
    List<ReceiverThreadEntity> toEntityList(List<ReceiverThreadDto> dtoList);
}