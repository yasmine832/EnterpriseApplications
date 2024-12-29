package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html
//howtodoinjava.com/spring-security/spring-security-tutorial


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean // in memory storage
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.builder()
                .username("user")
                .password("password")
                .roles("USER").build());
        return manager;
    }


    // to store and retrieve user data from db, connects db directly tru jdbc    @Bean
    //    public DataSource dataSource() {
    //        return DataSourceBuilder.create()
    //                .url("jdbc:mysql://localhost:3306/dailypuzzle")
    //                .username("root")
    //                //.password("")
    //                .driverClassName("com.mysql.cj.jdbc.Driver")
    //                .build();
    //
    //
    //    }


    @Bean // access control
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())// todo enable

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/products").permitAll() // allow all
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll() // Swagger UI access
                        .anyRequest().authenticated() //restrict any other endpoint
                       // .requestMatchers("/api/**").hasRole("USER") //by role
                        //todo
                )
                .formLogin(form -> form
                        //.loginPage("/api/auth/login"))//todo for custom
                        .defaultSuccessUrl("/hello/", true) //Todoo "/home"
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