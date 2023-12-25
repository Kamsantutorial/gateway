package com.example.gateway.config.security;

import com.example.gateway.dto.UserDetailDTO;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class DefaultAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        return Mono.defer(() -> Mono.just(webFilterExchange.getExchange().getResponse()).flatMap(response -> {
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            Map<String, Object> map = new HashMap<>(2);
            UserDetailDTO userDetails = (UserDetailDTO) authentication.getPrincipal();
            map.put("username", userDetails.getUsername());
            map.put("roles", userDetails.getAuthorities());
            DataBuffer dataBuffer = dataBufferFactory.wrap(map.toString().getBytes());
            return response.writeWith(Mono.just(dataBuffer));
        }));
    }
}
