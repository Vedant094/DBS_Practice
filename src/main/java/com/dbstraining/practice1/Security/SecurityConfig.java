package com.dbstraining.practice1.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final MyUserDetailsService myUserDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, MyUserDetailsService myUserDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // use global CorsConfig
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Public endpoints: login + registration endpoints for users/managers
        http.authorizeHttpRequests(auth -> auth
                //Public endpoints
                .requestMatchers("/auth/login","/auth/check-email").permitAll()
                .requestMatchers(HttpMethod.POST,"/managers/register").permitAll()
                .requestMatchers(HttpMethod.POST,"/users/{managerId}").permitAll()

                //Protected endpoints

                .requestMatchers("/managers/**").hasAuthority("ROLE_MANAGER")
                .requestMatchers("/users/**").hasAnyAuthority("ROLE_USER","ROLE_MANAGER")


                .anyRequest().authenticated()
        );

        // Optional: add CSP header if you really need it (fine to remove)
        http.headers(headers -> headers
                .contentSecurityPolicy(policy ->
                        policy.policyDirectives("default-src 'self'; connect-src 'self' http://localhost:4200"))
        );

        http.authenticationProvider(authenticationProvider());

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(myUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
