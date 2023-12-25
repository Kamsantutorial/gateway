package com.example.gateway.config.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.sleuth.Span;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class UserHeaderFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .flatMap(c -> {
                    Authentication authentication = c.getAuthentication();

                    OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();

                    Span span = exchange.getAttribute(Span.class.getName());
                    ServerHttpRequest request = exchange
                            .getRequest()
                            .mutate()
                            .header("user-id", principal.getAttributes().get("id").toString())
                            .header("authorities", principal.getAttributes().get("authorities").toString())
                            .header("trace_id", Objects.isNull(span) ? null : span.context().traceId())
                            .header("span_id", Objects.isNull(span) ? null : span.context().spanId())
                            .build();

                    ServerHttpResponse response = exchange.getResponse();
                    response.beforeCommit(() -> {
                        if (span != null) {
                            exchange.getResponse().getHeaders().add("trace_id", span.context().traceId());
                            exchange.getResponse().getHeaders().add("span_id", span.context().spanId());
                        }
                        return Mono.empty();
                    });

                    return chain.filter(exchange.mutate().request(request).response(response).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}
