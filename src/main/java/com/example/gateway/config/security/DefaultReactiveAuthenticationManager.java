package com.example.gateway.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

@Slf4j
public class DefaultReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.info("authentication {}", authentication);
        return Mono.just(authentication)
                .map(auth -> new UsernamePasswordAuthenticationToken(auth, null, auth.getAuthorities()));
    }
}
