package org.api.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class PreFilter implements GlobalFilter, Ordered {

    final Logger logger = LoggerFactory.getLogger(PreFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        logger.info("Pre filter invoked");

        var requestPath = exchange.getRequest().getPath().toString();
        logger.info("Request path = " + requestPath);

        var headers = exchange.getRequest().getHeaders();
        var headerNames = headers.keySet();
        headerNames.forEach(headerName -> {
            var headerValue = headers.getFirst(headerName);
            logger.info(headerName + " " + headerValue);
        });

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
