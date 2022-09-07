package me.hoon.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        //exchange : ServerHttpRequest, ServerHttpResponse 를 사용할 수 있도록 지원한다.
        //OrderedGatewayFilter 는 필터마다 순서를 부여할 수 있다.
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging Filter baseMessage -> {}", config.getBaseMessage());

            if(config.isPreLogger()) {
                log.info("Logging Filter PRE filter: request id -> {}", request.getId());
            }

            //Custom Post filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if(config.isPostLogger()) {
                    log.info("Logging POST filter: response code -> {}", response.getStatusCode());
                }
            }));
        }, Ordered.LOWEST_PRECEDENCE);

        return filter;
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}