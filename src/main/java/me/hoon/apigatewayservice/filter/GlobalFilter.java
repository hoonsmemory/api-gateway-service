package me.hoon.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * GlobalFilter 는 Custom Filter 와는 다르게 전역에 사용되는 Filter 다.
 * 가장 먼저 시작되고, 가장 늦게 종료된다.
 */
@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        //Custom Pre Filter
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            //yml 파일에서 설정한 변수는 Config 를 통해 가져올 수 있다.
            log.info("Global Filter baseMessage -> {}", config.getBaseMessage());

            if(config.isPreLogger()) {
                log.info("Global Filter PRE filter: request id -> {}", request.getId());
            }

            //Custom Post filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if(config.isPostlogger()) {
                    log.info("Global POST filter: response code -> {}", response.getStatusCode());
                }
            }));
        };
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postlogger;
    }
}
