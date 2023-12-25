package com.example.gateway.service.impl;

import com.example.gateway.constant.CacheType;
import com.example.gateway.entity.SecurityRouteEntity;
import com.example.gateway.repository.RouteRepository;
import com.example.gateway.service.SecurityService;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.cache.CacheFlux;
import reactor.cache.CacheMono;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Chuob Bunthoeurn
 */
@Service
@Slf4j
@CacheConfig(cacheNames = {"routerCache"})
public class SecurityServiceImpl implements SecurityService {
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    private final LoadingCache<String, Object> cache;
    private final LoadingCache<String, Object> caches;


    public SecurityServiceImpl() {
        this.cache = Caffeine
                .newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .refreshAfterWrite(1, TimeUnit.MINUTES)
                .build(this::findById);
        this.caches = Caffeine
                .newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .refreshAfterWrite(1, TimeUnit.MINUTES)
                .build(key -> this.findAll());
    }

    @Override
    public Flux<SecurityRouteEntity> findAll() {
        return CacheFlux
                .lookup(caches.asMap(), CacheType.ROUTE_SECURITY.getName(), SecurityRouteEntity.class)
                .onCacheMissResume(routeRepository.findAll());
    }

    @Override
    public Mono<SecurityRouteEntity> findByPath(String path) {
        return CacheMono.lookup(cache.asMap(), CacheType.ROUTE_SECURITY.getName(), SecurityRouteEntity.class)
                .onCacheMissResume(() -> routeRepository.findByPath(path)).cast(SecurityRouteEntity.class);
    }

    public Mono<SecurityRouteEntity> findById(String id) {
        return CacheMono.lookup(cache.asMap(), CacheType.ROUTE_SECURITY.getName(), SecurityRouteEntity.class)
                .onCacheMissResume(() -> routeRepository.findById(Long.valueOf(id))).cast(SecurityRouteEntity.class);
    }

    @SneakyThrows
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        return authentication.map(auth -> {
            ServerWebExchange exchange = authorizationContext.getExchange();
            ServerHttpRequest request = exchange.getRequest();
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                String authorityAuthority = authority.getAuthority();
                String path = request.getURI().getPath();

                log.info("Found authorityAuthority: {}", authorityAuthority);
                log.info("Found path: {}", path);

                try {
                    SecurityRouteEntity route = this.findAll().filter(t -> antPathMatcher.match(t.getPath(), path)
                                    && this.checkRole(t.getAuthority(), authorityAuthority))
                            .takeLast(1)
                            .last()
                            .switchIfEmpty(Mono.empty())
                            .toFuture()
                            .get();
                    if (!Objects.isNull(route)) {
                        log.info("Route matched {}", route);
                        return new AuthorizationDecision(true);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Error logging ", e);
                    return new AuthorizationDecision(false);
                }
            }
            return new AuthorizationDecision(false);
        }).defaultIfEmpty(new AuthorizationDecision(false));

    }

    private boolean checkRole(String routeRole, String userRole) {
        if (Objects.isNull(routeRole) || Objects.isNull(userRole))
            return false;
        String[] items = routeRole.split("\\s*,\\s*");
        for (String role : items) {
            if (antPathMatcher.match(role, userRole)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void refreshRoutes() {
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }
}
