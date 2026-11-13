package com.tdea.laboratoriobanco.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Allows only the local Vite origin while the development profile is active. */
@Configuration
@Profile("dev")
public class DevelopmentCorsConfig implements WebMvcConfigurer {
    /**
     * Permits the frontend's query and transfer requests on API routes.
     * @param registry Spring MVC CORS mappings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST")
                .allowedHeaders("Content-Type", "Accept")
                .exposedHeaders("Location")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
