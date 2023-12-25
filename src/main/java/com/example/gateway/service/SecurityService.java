package com.example.gateway.service;

import com.example.gateway.entity.SecurityRouteEntity;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Chuob Bunthoeurn
 */
public interface SecurityService {
    void refreshRoutes();

    Flux<SecurityRouteEntity> findAll();

    Mono<SecurityRouteEntity> findByPath(String path);

    Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext request);
}
