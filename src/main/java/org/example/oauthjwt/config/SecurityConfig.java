package org.example.oauthjwt.config;

import jakarta.servlet.http.HttpServletRequest;
import org.example.oauthjwt.oauth2.CustomSuccessHandler;
import org.example.oauthjwt.service.CustomOauth2UserService;
import org.example.oauthjwt.util.JWTFilter;
import org.example.oauthjwt.util.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    public SecurityConfig(CustomOauth2UserService customOauth2UserService, CustomSuccessHandler customSuccessHandler, JWTUtil jwtUtil) {

        this.customOauth2UserService = customOauth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // cors 설정을 위해서, security cors와 mvcConfig에서의 cors 두 개 다 설정해줘야 한다.
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://localhost:5173"));
                configuration.setAllowedMethods(Arrays.asList("*"));
                configuration.setAllowCredentials(true);
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setMaxAge(3600L);
                configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization")); // 한 번만 호출하고 모든 노출해야 할 헤더를 포함시킵니다.
                return configuration;
            }
        }));
        //csrf disable
        http.csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http.formLogin((auth) -> auth.disable());

        //HTTP Basic 인증 방식 disable
        http.httpBasic((auth) -> auth.disable());

        //JWTFilter 추가
        http.addFilterAfter(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //oauth2
        http.oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOauth2UserService))
                .successHandler(customSuccessHandler));

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/reissue").permitAll()
                        .anyRequest().authenticated());

        //세션 설정 : STATELESS
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}

