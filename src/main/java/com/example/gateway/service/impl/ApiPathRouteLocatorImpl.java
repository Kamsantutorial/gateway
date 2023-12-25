package com.example.gateway.service.impl;

import com.example.gateway.config.filter.RequestHashingGatewayFilterFactory;
import com.example.gateway.entity.SecurityRouteEntity;
import com.example.gateway.service.ApiRouteService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.BooleanSpec;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.sleuth.Tracer;
import reactor.core.publisher.Flux;

import java.util.Objects;

@AllArgsConstructor
@Slf4j
public class ApiPathRouteLocatorImpl implements RouteLocator {
    private final ApiRouteService apiRouteService;
    private final RouteLocatorBuilder routeLocatorBuilder;
    private final RequestHashingGatewayFilterFactory.Config config;
    private final RequestHashingGatewayFilterFactory requestHashingGatewayFilter;

    private final Tracer tracer;

    @Override
    public Flux<Route> getRoutes() {
        RouteLocatorBuilder.Builder routesBuilder = routeLocatorBuilder.routes();
        return apiRouteService.findApiRoutes()
                .map(apiRoute -> routesBuilder.route(String.valueOf(apiRoute.getId()),
                        predicateSpec -> setPredicateSpec(apiRoute, predicateSpec)))
                .collectList()
                .flatMapMany(builders -> routesBuilder.build()
                        .getRoutes());
    }

    private Buildable<Route> setPredicateSpec(SecurityRouteEntity apiRoute, PredicateSpec predicateSpec) {
        GatewayFilter gatewayFilter = this.requestHashingGatewayFilter.apply(config);
        BooleanSpec booleanSpec = predicateSpec.path(apiRoute.getPath());
        if (!Objects.isNull(apiRoute.getMethod()) && !apiRoute.getMethod().isEmpty()) {
            booleanSpec.and()
                    .method(apiRoute.getMethod());
        }
        booleanSpec.filters(spec -> spec.filter(gatewayFilter));
        log.info("apiRoute {}", apiRoute);
        return booleanSpec.uri(apiRoute.getUri());
    }
}
