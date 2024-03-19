package com.mj.mysns.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OidcUserService customOidcUserPersistenceService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http
            .csrf(csrf -> csrf.disable());
        http
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/posts").authenticated()
//                .requestMatchers("/user").authenticated()
                .anyRequest().permitAll());
//        http
//            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
//                SessionCreationPolicy.STATELESS));
        http
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(customOidcUserPersistenceService))
                .successHandler(this.customOAuth2AuthenticationSuccessHandler())
            );

//        http
//            .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("http://localhost:3333/realms/master");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:5500"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);
        return urlBasedCorsConfigurationSource;
    }

    @Bean
    AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler() {
        return new CustomizedOAuth2AuthenticationSuccessHandler();
    }
}
