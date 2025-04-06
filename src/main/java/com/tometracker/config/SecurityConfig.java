package com.tometracker.config;

import com.tometracker.security.RestAuthenticationFilter;
import com.tometracker.security.SecurityContextLoggingFilter;
import com.tometracker.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import java.util.List;

import static com.tometracker.domain.ControllerNames.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationEntryPoint entryPoint;
    private final UserService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(AuthenticationEntryPoint entryPoint, UserService userDetailsService, PasswordEncoder passwordEncoder) {
        this.entryPoint = entryPoint;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authConfig) throws Exception {
        AuthenticationManager authenticationManager = authConfig.getAuthenticationManager();

        TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices("uniqueAndSecretKey", userDetailsService);
        rememberMeServices.setTokenValiditySeconds(1209600);
        rememberMeServices.setParameter("remember-me");

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(registry -> registry
                    .requestMatchers("/gbooks/**").permitAll()
                    .requestMatchers("/users/books/*/reviews").permitAll()
                    .requestMatchers(REGISTRATION_URL, "/users/login").permitAll()
                    .anyRequest().authenticated())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
            .addFilterBefore(new SecurityContextLoggingFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new RestAuthenticationFilter(authenticationManager, rememberMeServices), UsernamePasswordAuthenticationFilter.class)
            .rememberMe(rememberMe -> rememberMe
                    .userDetailsService(userDetailsService)
                    .rememberMeServices(rememberMeServices)
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8080","http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public TokenBasedRememberMeServices rememberMeServices(UserService userDetailsService) {
        TokenBasedRememberMeServices services = new TokenBasedRememberMeServices("uniqueAndSecretKey", userDetailsService);
        services.setTokenValiditySeconds(1209600);
        services.setParameter("remember-me");
        return services;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
