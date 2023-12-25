package com.example.gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Primary
@Slf4j
public class GatewayErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = super.getError(request);
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("message", error.getMessage());
        errorAttributes.put("method", request.methodName());
        errorAttributes.put("path", request.path());
        MergedAnnotation<ResponseStatus> responseStatusAnnotation = MergedAnnotations
                .from(error.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ResponseStatus.class);
        HttpStatus errorStatus = determineHttpsStatus(error, responseStatusAnnotation);
        // Must set, otherwise an error will be reported because the RendereRRRRESPONSE method of DefaultErrorWebexceptionHandler gets this property, re-implementing defaultErrorWebexceptionHandler.
        errorAttributes.put("status", errorStatus.value());
        errorAttributes.put("code", errorStatus.value());
        // html view
        errorAttributes.put("timestamp", new Date());
        // html view
        errorAttributes.put("requestId", request.exchange().getRequest().getId());
        errorAttributes.put("error", errorStatus.getReasonPhrase());
        errorAttributes.put("exception", error.getClass().getName());
        log.error("Error ", error);
        return errorAttributes;
    }

    // Copy from DefaultErrorWebexceptionHandler
    private HttpStatus determineHttpsStatus(Throwable error, MergedAnnotation<ResponseStatus> responseStatusAnnotation) {
        HttpStatus result;
        if (error instanceof ResponseStatusException) {
            result = ((ResponseStatusException) error).getStatus();
        } else {
            result = responseStatusAnnotation.getValue("code", HttpStatus.class).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

}
