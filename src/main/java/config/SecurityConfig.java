package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


//https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html
//howtodoinjava.com/spring-security/spring-security-tutorial


@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean // access control
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())// todo

                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/products/**").permitAll() // allow all
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                       .anyRequest().authenticated() //restrict any other endpoint
                               ///.anyRequest().permitAll() // TODO
                       // .requestMatchers("/api/**").hasRole("USER") //by role
                        //todo
                )

                .formLogin(form -> form
                        .loginProcessingUrl("/login")
                        .loginPage("/login")

                        .defaultSuccessUrl("/api/auth/current", false)
                        .failureUrl("/login?error=true")
                )


                // logout behavior
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        //.logoutSuccessUrl("/login")
                        // Remove the session cookie from client browser
                        .deleteCookies("JSESSIONID") //springs default session cookie name
                        // Clear  server-side session data
                        .invalidateHttpSession(true)//to ensure no session data remains after logout
                )


        // Session management configuration
                 .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // Create session when needed
               // .invalidSessionUrl("/api/auth/login")  // Redirect on invalid session
                .maximumSessions(1)  // Prevent multiple logins
                .maxSessionsPreventsLogin(false)  // New login expires old session
        );


        return http.build();
    }

    @Bean //for password encoding
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean //for handling uthentication
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}