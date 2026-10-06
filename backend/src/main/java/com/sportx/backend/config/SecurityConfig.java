package com.sportx.backend.config;

import com.sportx.backend.security.CustomUserDetailsService;
import com.sportx.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)) // For H2 console
                .authorizeHttpRequests(auth -> auth
                        // Public Auth & Docs
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        // Public Read-Only endpoints for Tournament Public View / Live scores
                        .requestMatchers(HttpMethod.GET, "/api/v1/tournaments/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/matches/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/standings/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/sports/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/venues/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/teams/**").permitAll()
                        // Tournament Administration
                        .requestMatchers(HttpMethod.POST, "/api/v1/tournaments/**").hasAnyRole("ADMIN", "ORGANIZER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/tournaments/**").hasAnyRole("ADMIN", "ORGANIZER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tournaments/**").hasRole("ADMIN")
                        // Referee & Live Match Scoring
                        .requestMatchers("/api/v1/matches/*/score-events").hasAnyRole("ADMIN", "ORGANIZER", "REFEREE")
                        .requestMatchers("/api/v1/matches/*/finalize").hasAnyRole("ADMIN", "ORGANIZER", "REFEREE")
                        // Team Registration & Management
                        .requestMatchers(HttpMethod.POST, "/api/v1/teams/**").hasAnyRole("ADMIN", "ORGANIZER", "PLAYER")
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
