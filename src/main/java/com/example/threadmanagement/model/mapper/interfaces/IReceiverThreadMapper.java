package com.example.threadmanagement.model.mapper.interfaces;

import com.example.threadmanagement.model.dto.ReceiverThreadDto;
import com.example.threadmanagement.model.entity.ReceiverThreadEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface IReceiverThreadMapper {
    ISenderThreadMapper INSTANCE = Mappers.getMapper(ISenderThreadMapper.class);

    ReceiverThreadDto toDto(ReceiverThreadEntity entity);
    ReceiverThreadEntity toEntity(ReceiverThreadDto dto);
    List<ReceiverThreadDto> toDtoList(List<ReceiverThreadEntity> entityList);
    List<ReceiverThreadEntity> toEntityList(List<ReceiverThreadDto> dtoList);
}