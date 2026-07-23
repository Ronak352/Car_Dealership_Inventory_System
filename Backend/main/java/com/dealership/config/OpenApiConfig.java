package com.dealership.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Central OpenAPI / Swagger configuration.
 *
 * Exposes API metadata and registers a reusable "bearerAuth" security
 * scheme so every JWT-protected endpoint can opt in with a single
 * {@code @SecurityRequirement(name = "bearerAuth")} annotation instead
 * of repeating the scheme definition per controller.
 *
 * Swagger UI:   /swagger-ui/index.html
 * OpenAPI spec: /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI carDealershipOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME_NAME, bearerAuthScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title("Car Dealership Inventory Management System API")
                .description("""
                        REST API for a 360-degree automobile dealership platform:
                        vehicle inventory, customers, employees/salespersons,
                        vehicle sales and purchases, payments, loans, test drives,
                        wishlists, inventory movement and sales reporting.

                        Authentication: obtain a JWT from POST /api/auth/login,
                        then authorize requests with 'Bearer <token>'.
                        """)
                .version("v1.0")
                .contact(new Contact()
                        .name("Car Dealership Engineering Team")
                        .email("engineering@dealership.example.com"))
                .license(new License().name("Proprietary"));
    }

    private SecurityScheme bearerAuthScheme() {
        return new SecurityScheme()
                .name(BEARER_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Provide the JWT issued by /api/auth/login as: Bearer <token>");
    }
}
