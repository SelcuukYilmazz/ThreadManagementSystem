package com.example.threadmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Marks this class as a Spring configuration class for web-related settings.
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures Cross-Origin Resource Sharing (CORS) settings for the application.
     * - CORS is necessary when a frontend (e.g., React) hosted on a different domain (e.g., localhost:3000)
     *   communicates with the backend (e.g., localhost:8080).
     *
     * @param registry A registry to define CORS configurations for specific paths.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply CORS settings to all endpoints.
                .allowedOrigins("http://localhost:3000") // Allow requests from the specified origin (frontend).
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Specify the HTTP methods allowed.
                .allowedHeaders("*") // Allow all HTTP headers in requests.
                .allowCredentials(true); // Allow cookies and credentials in cross-origin requests.
    }

    /**
     * Configures resource handlers for serving static resources.
     * - This is particularly useful for serving Swagger UI and webjars in the application.
     * - Swagger UI provides a graphical interface to interact with API endpoints.
     *
     * @param registry A registry to define resource handlers for static content.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map requests for "swagger-ui.html" to its location within the application.
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/"); // Location of Swagger UI resources.

        // Map requests for webjars (like Swagger dependencies) to their location within the application.
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/"); // Location of webjar resources.
    }
}
