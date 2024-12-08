package com.example.threadmanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration // Marks this class as a configuration class for Spring
public class SwaggerConfig {

    /**
     * Configures the OpenAPI documentation for the application.
     * This bean creates and customizes the OpenAPI specification with details like API info,
     * server environments, licensing, and contact details.
     *
     * @return An OpenAPI object with the configured documentation details.
     */
    @Bean
    public OpenAPI openAPI() {
        // Define the development server configuration
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080"); // Localhost URL for development environment
        devServer.setDescription("Development environment"); // Brief description for developers

        // Define the production server configuration
        Server prodServer = new Server();
        prodServer.setUrl("https://your-production-url.com"); // Replace with the production URL
        prodServer.setDescription("Production environment"); // Description for the production server

        // Define contact information for the API
        Contact contact = new Contact()
                .name("Thread Management Team") // Name of the contact person or team
                .email("contact@example.com") // Contact email for queries
                .url("https://www.example.com"); // Website for more information

        // Define the license information for the API
        License mitLicense = new License()
                .name("MIT License") // Name of the license
                .url("https://choosealicense.com/licenses/mit/"); // URL to the full license details

        // Define basic information about the API
        Info info = new Info()
                .title("Thread Management API") // Title of the API
                .version("1.0") // Current version of the API
                .contact(contact) // Add the previously defined contact info
                .description("This API exposes endpoints to manage threads.") // Brief description of the API
                .termsOfService("https://www.example.com/terms") // URL to the terms of service
                .license(mitLicense); // Attach the license info

        // Combine all the information into an OpenAPI object and include the server configurations
        return new OpenAPI()
                .info(info) // Add the API information
                .servers(List.of(devServer, prodServer)); // Add the server configurations
    }
}
