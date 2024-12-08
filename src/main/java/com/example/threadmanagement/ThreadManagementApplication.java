package com.example.threadmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableWebMvc
@EnableScheduling
@OpenAPIDefinition(
		info = @Info(
				title = "Thread Management System API",
				version = "1.0",
				description = "API for managing sender and receiver threads"
		)
)
@ComponentScan(basePackages = "com.example.threadmanagement")
public class ThreadManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThreadManagementApplication.class, args);
	}
}