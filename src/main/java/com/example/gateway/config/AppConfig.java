package com.example.gateway.config;

import brave.baggage.*;
import com.example.gateway.config.filter.RequestHashingGatewayFilterFactory;
import com.example.gateway.service.ApiRouteService;
import com.example.gateway.service.impl.ApiPathRouteLocatorImpl;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;

import java.security.NoSuchAlgorithmException;

@Configuration
@EnableCaching
@EnableR2dbcRepositories(basePackages = "com.example.gateway")
public class AppConfig {

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-secret}")
    private String clientSecret;
    @Autowired
    private RequestHashingGatewayFilterFactory requestHashingGatewayFilter;

    @Bean
    public DatabaseClient dbClient(ConnectionFactory connectionFactory) {
        return DatabaseClient.builder()
                .connectionFactory(connectionFactory)
                .namedParameters(true)
                .build();
    }

    @Bean
    public RouteLocator routeLocator(ApiRouteService apiRouteService,
                                     RouteLocatorBuilder routeLocatorBuilder, Tracer tracer) throws NoSuchAlgorithmException {
        RequestHashingGatewayFilterFactory.Config config = new RequestHashingGatewayFilterFactory.Config();
        config.setAlgorithm("SHA-512");
        return new ApiPathRouteLocatorImpl(apiRouteService, routeLocatorBuilder, config, requestHashingGatewayFilter, tracer);
    }

    @Bean
    public BaggagePropagationCustomizer baggagePropagationCustomizer() {
        return (factoryBuilder) -> {
            factoryBuilder.add(
                    BaggagePropagationConfig.SingleBaggageField.remote(BaggageField.create("Correlation-Id")));
        };
    }

    @Bean
    public CorrelationScopeCustomizer correlationScopeCustomizer() {
        return builder -> {
            builder.add(CorrelationScopeConfig.SingleCorrelationField.newBuilder(BaggageField.create("Correlation-Id"))
                    .flushOnUpdate()
                    .build());
        };
    }


}
