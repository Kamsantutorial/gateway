package com.example.gateway.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CacheType {
    ROUTE_SECURITY("route_security");

    private final String name;
}
