package com.example.threadmanagement.model.mapper.interfaces;

import com.example.threadmanagement.model.dto.SenderThreadDto;
import com.example.threadmanagement.model.entity.SenderThreadEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ISenderThreadMapper {
    ISenderThreadMapper INSTANCE = Mappers.getMapper(ISenderThreadMapper.class);

    SenderThreadDto toDto(SenderThreadEntity entity);
    SenderThreadEntity toEntity(SenderThreadDto dto);
    List<SenderThreadDto> toDtoList(List<SenderThreadEntity> entityList);
    List<SenderThreadEntity> toEntityList(List<SenderThreadDto> dtoList);
}