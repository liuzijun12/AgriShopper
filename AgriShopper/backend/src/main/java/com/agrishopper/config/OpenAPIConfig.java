package com.agrishopper.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AgriShopper API Documentation")
                        .description("API documentation for AgriShopper backend services")
                        .version("1.0")
                        .contact(new Contact()
                                .name("AgriShopper Team")
                                .email("support@agrishopper.com")));
    }
} 