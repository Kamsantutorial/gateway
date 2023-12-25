package com.example.gateway.config.security;

import com.example.gateway.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.NimbusReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.CrossOriginOpenerPolicyServerHttpHeadersWriter;
import org.springframework.security.web.server.header.CrossOriginResourcePolicyServerHttpHeadersWriter;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {

    @Autowired
    private DefaultAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private SecurityService securityService;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.introspection-uri}")
    private String tokenUri;
    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-secret}")
    private String clientSecret;
    @Value("${spring.allow.cross.origin.urls}")
    private String allowCrossOriginUrls;

    @Bean
    public ReactiveOpaqueTokenIntrospector introspect() {
        return new CustomAuthoritiesOpaqueTokenIntrospector(new NimbusReactiveOpaqueTokenIntrospector(tokenUri, clientId, clientSecret));
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authenticationManager(new DefaultReactiveAuthenticationManager())
                .securityContextRepository(new DefaultSecurityContextRepository())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/oauth/**").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange().access(new DefaultReactiveAuthorizationManager(securityService))
                )
                .formLogin()
                .authenticationSuccessHandler(new DefaultAuthenticationSuccessHandler())
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .oauth2ResourceServer()
                .bearerTokenConverter(new ServerBearerTokenConverter())
                .opaqueToken(spec -> spec.introspector(introspect()))
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler((exchange, exception) -> Mono.error(exception))
                .and()
                .headers(
                        headers ->
                                headers
                                        .crossOriginOpenerPolicy(
                                                crossOriginOpenerPolicySpec -> crossOriginOpenerPolicySpec.policy(CrossOriginOpenerPolicyServerHttpHeadersWriter.CrossOriginOpenerPolicy.SAME_ORIGIN)
                                        )
                                        .crossOriginResourcePolicy(
                                                crossOriginResourcePolicySpec -> crossOriginResourcePolicySpec.policy(CrossOriginResourcePolicyServerHttpHeadersWriter.CrossOriginResourcePolicy.SAME_ORIGIN)
                                        )
                                        .permissionsPolicy(permissionsPolicySpec -> permissionsPolicySpec.policy(
                                                "accelerometer=(),ambient-light-sensor=(),autoplay=(),battery=(),camera=(),display-capture=(),document-domain=(),encrypted-media=(),fullscreen=(),gamepad=(),geolocation=(),gyroscope=(),layout-animations=(self),legacy-image-formats=(self),magnetometer=(),microphone=(),midi=(),oversized-images=(self),payment=(),picture-in-picture=(),publickey-credentials-get=(),speaker-selection=(),sync-xhr=(self),unoptimized-images=(self),unsized-media=(self),usb=(),screen-wake-lock=(),web-share=(),xr-spatial-tracking=()"
                                        ))
                                        .frameOptions(
                                                frameOptions ->
                                                        frameOptions.mode(XFrameOptionsServerHttpHeadersWriter.Mode.DENY)
                                        )
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfiguration() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.applyPermitDefaultValues();
        corsConfig.setAllowCredentials(true);
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("PATCH");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("OPTIONS");
        corsConfig.setAllowedOrigins(Arrays.asList(allowCrossOriginUrls.split(",")));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Requestor-Type"));
        corsConfig.setExposedHeaders(List.of("X-Get-Header"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        return new CorsWebFilter(corsConfiguration());
    }
}
