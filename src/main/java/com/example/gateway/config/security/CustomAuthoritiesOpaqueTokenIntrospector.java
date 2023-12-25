package com.example.gateway.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.NimbusReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomAuthoritiesOpaqueTokenIntrospector implements ReactiveOpaqueTokenIntrospector {
    private final ReactiveOpaqueTokenIntrospector delegate;

    public CustomAuthoritiesOpaqueTokenIntrospector(NimbusReactiveOpaqueTokenIntrospector nimbusReactiveOpaqueTokenIntrospector) {
        this.delegate = nimbusReactiveOpaqueTokenIntrospector;
    }

    public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {
        return this.delegate.introspect(token)
                .map(principal -> new DefaultOAuth2AuthenticatedPrincipal(
                        principal.getName(), principal.getAttributes(), extractAuthorities(principal)));
    }

    private Collection<GrantedAuthority> extractAuthorities(OAuth2AuthenticatedPrincipal principal) {
        List<String> roles = principal.getAttribute("authorities");
        assert roles != null;
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}