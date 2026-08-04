package com.seowonfc.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_KEY = "BearerAuth";

    @Bean
    public OpenAPI openAPI() {
        SecurityRequirement securityRequirement =
                new SecurityRequirement().addList(BEARER_KEY);

        return new OpenAPI()
                .info(new Info()
                        .title("서원 FC API")
                        .description("서원 FC 동호회 홈페이지 REST API 문서")
                        .version("v1.0.0"))
                .addSecurityItem(securityRequirement)
                .components(new Components().addSecuritySchemes(BEARER_KEY,
                        new SecurityScheme()
                                .name(BEARER_KEY)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}