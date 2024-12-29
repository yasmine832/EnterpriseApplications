package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
                .csrf(csrf -> csrf.disable())// todo enable

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/products").permitAll() // allow all
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated() //restrict any other endpoint
                       // .requestMatchers("/api/**").hasRole("USER") //by role
                        //todo
                )
                .formLogin(form -> form
                        //.loginPage("/api/auth/login"))//todo for custom
                        .defaultSuccessUrl("/api/auth/current", true) //Todoo "/home"
                        .failureUrl("/login?error=true")
                );

        //.formLogin(form -> form.disable())
        //            .httpBasic(basic -> basic.disable());

        //sessies todo

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