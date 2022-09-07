package me.hoon.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    //@Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        /**
         * cloud gateway routes 정보
         * http://localhost:8000/first-service/**, http://localhost:8000/second-service/**
         * 위처럼 요청이 들어올 경우 게이트웨이는 필터를 적용 후 진짜 요청할 서버에 대신 요청을 해주는 프록시 역할을 한다.
         * http://localhost:8081/first-service/**l, http://localhost:8082/second-service/**
         */
        return builder.routes()
                .route(r -> r.path("/first-service/**")
                             .filters(f -> f.addRequestHeader("first-request", "first-request-header")
                                            .addResponseHeader("first-response", "first-response-header"))
                             .uri("http://localhost:8081"))
                .route(r -> r.path("/second-service/**")
                        .filters(f -> f.addRequestHeader("second-request", "second-request-header")
                                .addResponseHeader("second-response", "second-response-header"))
                        .uri("http://localhost:8082"))
                .build();
    }
}
