package main.java.rmit.saintgiong.jmgateway.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Allow these origins
        corsConfig.setAllowedOrigins(Arrays.asList(
                "https://jm.saintgiong.ttr.gg",
                "http://localhost:3000",
                "http://localhost:3001"));

        // Allow all HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allow common headers
        corsConfig.setAllowedHeaders(Arrays.asList(
                "Content-Type",
                "Authorization",
                "Cookie",
                "X-Requested-With",
                "Accept",
                "Origin"));

        // Allow credentials (cookies)
        corsConfig.setAllowCredentials(true);

        // Expose Set-Cookie header to frontend
        corsConfig.setExposedHeaders(List.of("Set-Cookie"));

        // Cache preflight response for 1 hour
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
