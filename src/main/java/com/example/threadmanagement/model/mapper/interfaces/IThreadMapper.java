package com.example.threadmanagement.model.mapper.interfaces;

import com.example.threadmanagement.model.dto.ThreadDto;
import com.example.threadmanagement.model.entity.ThreadEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface IThreadMapper {
    IThreadMapper INSTANCE = Mappers.getMapper(IThreadMapper.class);

    ThreadDto toDto(ThreadEntity entity);
    ThreadEntity toEntity(ThreadDto dto);
    List<ThreadDto> toDtoList(List<ThreadEntity> entityList);
    List<ThreadEntity> toEntityList(List<ThreadDto> dtoList);
}