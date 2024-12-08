package com.example.threadmanagement.model.mapper.interfaces;

import com.example.threadmanagement.model.dto.ReceiverThreadDto;
import com.example.threadmanagement.model.entity.ReceiverThreadEntity;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface IReceiverThreadMapper {

    ReceiverThreadDto toDto(ReceiverThreadEntity entity);
    ReceiverThreadEntity toEntity(ReceiverThreadDto dto);
    List<ReceiverThreadDto> toDtoList(List<ReceiverThreadEntity> entityList);
    List<ReceiverThreadEntity> toEntityList(List<ReceiverThreadDto> dtoList);
}