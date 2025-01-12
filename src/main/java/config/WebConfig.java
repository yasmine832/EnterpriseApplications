package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allows CORS for paths starting with /api/
                .allowedOrigins("http://localhost:3001")
                .allowedMethods("*") // Allow all methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true);


    }
}
