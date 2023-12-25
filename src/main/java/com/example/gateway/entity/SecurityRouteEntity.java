package com.example.gateway.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "security_route") // To bind our model class with a database table with defined name
public class SecurityRouteEntity {
    @Id
    private Long id;
    private String path;
    private String method;
    private String uri;
    private int order;
    private String authority;
    private String type;

}
