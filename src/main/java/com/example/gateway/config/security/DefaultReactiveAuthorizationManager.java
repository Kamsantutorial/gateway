package com.example.gateway.config.security;

import com.example.gateway.service.SecurityService;
import com.example.gateway.vo.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

@Slf4j
public class DefaultReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final SecurityService service;

    public DefaultReactiveAuthorizationManager(SecurityService service) {
        this.service = service;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        return service.check(authentication, authorizationContext);
    }

    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
        return check(authentication, object)
                .filter(AuthorizationDecision::isGranted)
                .switchIfEmpty(Mono.defer(() -> {
                    ResponseMessage<Object> responseMessage = new ResponseMessage<>();
                    responseMessage.error(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Access denied");
                    return Mono.error(new AccessDeniedException(responseMessage.responseString()));
                })).flatMap(d -> Mono.empty());
    }
}
