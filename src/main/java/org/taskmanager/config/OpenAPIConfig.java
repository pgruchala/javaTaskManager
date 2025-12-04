package org.taskmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI taskManagerOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Task manager API")
                        .description("System for managing tasks for multiple users")
                        .version("1.0")
                );
    }
}
