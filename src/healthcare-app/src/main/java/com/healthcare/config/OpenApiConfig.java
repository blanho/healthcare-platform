package com.healthcare.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:Healthcare Platform}")
    private String applicationName;

    @Bean
    public OpenAPI healthcareOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Healthcare Platform API")
                .description("HIPAA-compliant healthcare platform REST API")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Healthcare Platform Team")
                    .email("support@healthcare-platform.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://healthcare-platform.com/license")))
            .servers(List.of(
                new Server().url("/").description("Default Server")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .name("bearerAuth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Authorization header using the Bearer scheme")));
    }
}
