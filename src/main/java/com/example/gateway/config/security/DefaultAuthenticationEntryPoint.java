package com.example.gateway.config.security;

import com.example.gateway.constant.ErrorCode;
import com.example.gateway.vo.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

@Slf4j
@Component
public class DefaultAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        return Mono.defer(() -> Mono.just(exchange.getResponse())).flatMap(response -> {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            ResponseMessage<Object> responseMessage = new ResponseMessage<>();
            responseMessage.error(String.valueOf(HttpStatus.UNAUTHORIZED.value()), ex.getMessage());

            log.error("Error logging", ex);
            if (ex instanceof AuthenticationServiceException) {
                responseMessage.error(ErrorCode.E401.name(), ErrorCode.E401.getDesc());
            }
            DataBuffer buffer = dataBufferFactory.wrap(responseMessage.responseString().getBytes(
                    Charset.defaultCharset()));
            return response.writeWith(Mono.just(buffer));
        });
    }
}
