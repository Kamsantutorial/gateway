package com.example.gateway.vo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Chuob Bunthoeurn
 */
@Data
@Slf4j
public class ResponseMessage<T> {

    private String code;
    private String message;
    private T body;

    public ResponseMessage<T> body(T response) {
        this.body = response;
        return this;
    }

    public ResponseMessage<T> success() {
        if (this.code == null) {
            this.setMessage("SUCCESS");
            this.setCode("200");
        }
        return this;
    }

    public ResponseMessage<T> error(String code, String message) {
        if (code != null && message != null) {
            this.setMessage(message);
            this.setCode(code);
        }
        return this;
    }

    public ResponseMessage<T> response() {
        return this;
    }

    public String responseString() {
        return String.format("{\n" +
                "    \"code\": \"%s\",\n" +
                "    \"message\": \"%s\",\n" +
                "    \"body\": %s\n" +
                "}", code, message, body);
    }


}
