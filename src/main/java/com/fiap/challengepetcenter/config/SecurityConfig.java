package com.fiap.challengepetcenter.config;

import com.fiap.challengepetcenter.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/"
                        ).permitAll()


                        // Públicos
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        // Tutor
                        .requestMatchers("/api/pets/**")
                        .hasRole("TUTOR")

                        .requestMatchers(HttpMethod.POST, "/api/solicitacoes")
                        .hasRole("TUTOR")

                        .requestMatchers(HttpMethod.GET, "/api/solicitacoes/pet/**")
                        .hasRole("TUTOR")

                        .requestMatchers(HttpMethod.GET, "/api/solicitacoes/user/**")
                        .hasRole("TUTOR")

                        .requestMatchers(HttpMethod.GET, "/api/pet-veterinarios/pet/**")
                        .hasRole("TUTOR")


                        // Tutor ou Veterinário
                        .requestMatchers(HttpMethod.GET, "/api/veterinarios")
                        .hasAnyRole("TUTOR", "VETERINARIO")

                        .requestMatchers(HttpMethod.GET, "/api/veterinarios/*")
                        .hasAnyRole("TUTOR", "VETERINARIO")

                        .requestMatchers(HttpMethod.GET, "/api/solicitacoes/*")
                        .hasAnyRole("TUTOR", "VETERINARIO")

                        .requestMatchers(HttpMethod.GET, "/api/pet-veterinarios/*")
                        .hasAnyRole("TUTOR", "VETERINARIO")


                        // Veterinário
                        .requestMatchers(HttpMethod.POST, "/api/veterinarios")
                        .hasRole("VETERINARIO")

                        .requestMatchers(HttpMethod.PUT, "/api/veterinarios/*")
                        .hasRole("VETERINARIO")

                        .requestMatchers(HttpMethod.DELETE, "/api/veterinarios/*")
                        .hasRole("VETERINARIO")

                        .requestMatchers(HttpMethod.GET, "/api/veterinarios/user/**")
                        .hasRole("VETERINARIO")

                        .requestMatchers(HttpMethod.GET, "/api/solicitacoes/veterinario/**")
                        .hasRole("VETERINARIO")

                        .requestMatchers(HttpMethod.PATCH, "/api/solicitacoes/*/aceitar")
                        .hasRole("VETERINARIO")

                        .requestMatchers(HttpMethod.PATCH, "/api/solicitacoes/*/recusar")
                        .hasRole("VETERINARIO")

                        .requestMatchers(HttpMethod.GET, "/api/pet-veterinarios/veterinario/**")
                        .hasRole("VETERINARIO")

                        .requestMatchers(HttpMethod.DELETE, "/api/pet-veterinarios/*")
                        .hasRole("VETERINARIO")


                        // Demais endpoints: precisam estar autenticados
                        .anyRequest().authenticated()

                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
