// config/MapperConfig.java
package com.example.threadmanagement.config;

import com.example.threadmanagement.model.mapper.interfaces.ISenderThreadMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ISenderThreadMapper iThreadMapper() {
        return ISenderThreadMapper.INSTANCE;
    }
}