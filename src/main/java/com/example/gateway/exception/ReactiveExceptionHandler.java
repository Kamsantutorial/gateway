/*
package com.example.gateway.exception;

import com.example.gateway.vo.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

*/
/**
 * @author Chuob Bunthoeurn
 *//*

@Slf4j
@Component
public class ReactiveExceptionHandler extends AbstractErrorWebExceptionHandler {
    private final Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode;
    private final HttpStatus defaultStatus;

    public ReactiveExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources,
                                    ApplicationContext applicationContext, Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode,
                                    HttpStatus defaultStatus) {
        super(errorAttributes, resources, applicationContext);
        this.exceptionToStatusCode = exceptionToStatusCode;
        this.defaultStatus = defaultStatus;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

        Throwable error = getError(request);
        log.error("An error has been occurred", error);
        HttpStatus httpStatus;
        if (error instanceof Exception) {
            httpStatus = exceptionToStatusCode.getOrDefault(error.getClass(), defaultStatus);
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        ResponseMessage<Object> responseMessage = new ResponseMessage<>();
        responseMessage.error(String.valueOf(httpStatus.value()), error.getMessage());
        return ServerResponse
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(responseMessage));
    }
}
*/
