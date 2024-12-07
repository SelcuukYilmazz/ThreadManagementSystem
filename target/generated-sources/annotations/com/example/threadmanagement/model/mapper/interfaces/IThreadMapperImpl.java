package com.example.threadmanagement.model.mapper.interfaces;

import com.example.threadmanagement.model.dto.ThreadDto;
import com.example.threadmanagement.model.entity.ThreadEntity;
import com.example.threadmanagement.model.entity.ThreadState;
import com.example.threadmanagement.model.entity.ThreadType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-07T23:51:37+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class IThreadMapperImpl implements IThreadMapper {

    @Override
    public ThreadDto toDto(ThreadEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        ThreadType type = null;
        ThreadState state = null;
        Integer priority = null;

        id = entity.getId();
        type = entity.getType();
        state = entity.getState();
        priority = entity.getPriority();

        ThreadDto threadDto = new ThreadDto( id, type, state, priority );

        return threadDto;
    }

    @Override
    public ThreadEntity toEntity(ThreadDto dto) {
        if ( dto == null ) {
            return null;
        }

        ThreadEntity threadEntity = new ThreadEntity();

        threadEntity.setId( dto.getId() );
        threadEntity.setType( dto.getType() );
        threadEntity.setState( dto.getState() );
        threadEntity.setPriority( dto.getPriority() );

        return threadEntity;
    }

    @Override
    public List<ThreadDto> toDtoList(List<ThreadEntity> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<ThreadDto> list = new ArrayList<ThreadDto>( entityList.size() );
        for ( ThreadEntity threadEntity : entityList ) {
            list.add( toDto( threadEntity ) );
        }

        return list;
    }

    @Override
    public List<ThreadEntity> toEntityList(List<ThreadDto> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<ThreadEntity> list = new ArrayList<ThreadEntity>( dtoList.size() );
        for ( ThreadDto threadDto : dtoList ) {
            list.add( toEntity( threadDto ) );
        }

        return list;
    }
}
