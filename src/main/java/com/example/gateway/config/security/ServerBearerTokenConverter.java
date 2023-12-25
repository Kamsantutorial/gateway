package com.example.gateway.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class ServerBearerTokenConverter extends ServerBearerTokenAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return super.convert(exchange);
    }


}
