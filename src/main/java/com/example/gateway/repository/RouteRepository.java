package com.example.gateway.repository;

import com.example.gateway.entity.SecurityRouteEntity;
import lombok.NonNull;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RouteRepository extends ReactiveCrudRepository<SecurityRouteEntity, Long> {
    Mono<SecurityRouteEntity> findByPath(String path);

    @NonNull
    Flux<SecurityRouteEntity> findAll();

    @Query(value = "select s.* from security_route s where s.type='ROUTE'")
    Flux<SecurityRouteEntity> findAllRouteLocator();
}
