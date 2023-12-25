package com.example.gateway.service.impl;

import com.example.gateway.constant.CacheType;
import com.example.gateway.entity.SecurityRouteEntity;
import com.example.gateway.repository.RouteRepository;
import com.example.gateway.service.ApiRouteService;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.cache.CacheFlux;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;

@Service
public class ApiRouteServiceImpl implements ApiRouteService {

    @Autowired
    private RouteRepository apiRouteRepository;
    private final LoadingCache<String, Object> caches;

    public ApiRouteServiceImpl() {
        this.caches = Caffeine
                .newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .refreshAfterWrite(1, TimeUnit.MINUTES)
                .build(key -> this.findApiRoutes());
    }

    @Override
    public Flux<SecurityRouteEntity> findApiRoutes() {
        return CacheFlux
                .lookup(caches.asMap(), CacheType.ROUTE_SECURITY.getName(), SecurityRouteEntity.class)
                .onCacheMissResume(apiRouteRepository.findAllRouteLocator());
    }
}
