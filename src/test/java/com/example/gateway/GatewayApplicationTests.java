package com.example.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Slf4j
@TestPropertySource(value = "classpath:application-test.yml")
public abstract class GatewayApplicationTests {
}
