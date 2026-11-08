package com.example.umad_music.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

// Define desde que paginas web se puede llamar a la API.
// localhost siempre esta permitido para desarrollo; el frontend publicado se pone en FRONTEND_URL.
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String frontendUrl;

    public CorsConfig(@Value("${app.frontend-url}") String frontendUrl) {
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> origenes = new ArrayList<>(List.of("http://localhost:*", "http://127.0.0.1:*"));
        if (!frontendUrl.isBlank()) {
            // Un origen nunca lleva "/" al final, se lo quitamos por si acaso
            origenes.add(frontendUrl.trim().replaceAll("/+$", ""));
        }

        registry.addMapping("/api/**")
                .allowedOriginPatterns(origenes.toArray(new String[0]))
                .allowedMethods("GET", "POST", "DELETE");
    }
}
