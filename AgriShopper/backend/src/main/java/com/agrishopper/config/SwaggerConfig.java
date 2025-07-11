package com.agrishopper.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("AgriShopper API")
                        .description("AgriShopper application REST API documentation")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("AgriShopper Team")
                                .email("contact@agrishopper.com")));
    }
}
