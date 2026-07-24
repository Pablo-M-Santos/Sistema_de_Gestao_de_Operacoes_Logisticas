package br.com.logicore.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "LogiCore API",
                version = "1.0.0",
                description = """
                        REST API for Logistics Operations Management System.
                        
                        This API provides resources for managing:
                        - Departments
                        - Users
                        - Vehicles
                        - Drivers
                        - Warehouses
                        - Operations
                        """,
                contact = @Contact(
                        name = "LogiCore Development Team",
                        url = "https://github.com"
                ),
                license = @License(
                        name = "MIT License"
                )
        )
)
public class OpenApiConfig {
}