package com.tdea.laboratoriobanco.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/** OpenAPI metadata for the academic banking API. */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Bank Transfer Laboratory API", version = "1.0",
        description = "Academic API for account queries and atomic bank transfers. "
                + "No authentication is implemented. Do not expose publicly without security controls."))
public class OpenApiConfig {
}
