package com.ambrygen.adx.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI myAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .info(new Info()
                        .title("BhashaMitra REST APIs")
                        .description("Documentation of BhashaMitra REST APIs")
                        .version("1.0")
                );
        //.addSecurityItem(
        //                        new SecurityRequirement().addList("bearer-jwt", Arrays.asList("read", "write")));
    }
}
