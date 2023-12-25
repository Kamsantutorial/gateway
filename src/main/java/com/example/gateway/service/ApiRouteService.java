package com.example.gateway.service;

import com.example.gateway.entity.SecurityRouteEntity;
import reactor.core.publisher.Flux;

public interface ApiRouteService {
    Flux<SecurityRouteEntity> findApiRoutes();
}
