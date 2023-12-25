package com.example.gateway.exception;

/**
 * @author Chuob Bunthoeurn
 */
public class ServerException extends Exception {

    private final String message;
    private final String code;

    public ServerException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
