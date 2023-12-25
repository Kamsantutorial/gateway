package com.example.gateway.config.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

//@Component
public class DefaultSecurityContextRepository implements ServerSecurityContextRepository {

    public final static String TOKEN_HEADER = "Authorization";

    public final static String BEARER = "Bearer ";

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        List<String> headers = request.getHeaders().get(TOKEN_HEADER);
        if (!CollectionUtils.isEmpty(headers)) {
            String authorization = headers.get(0);
            if (!authorization.isEmpty()) {
                String token = authorization.substring(BEARER.length());
                if (!token.isEmpty()) {
                    Mono<Authentication> authenticate = new DefaultReactiveAuthenticationManager().authenticate(
                            new UsernamePasswordAuthenticationToken(token, null)
                    );
                    authenticate.map(SecurityContextImpl::new);
                }
            }
        }
        return Mono.empty();
    }
}
