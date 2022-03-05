package org.api.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

@Configuration
public class GlobalFilterConfiguration {

    final Logger logger = LoggerFactory.getLogger(GlobalFilterConfiguration.class);

    @Bean
    @Order(1)
    public GlobalFilter secondFilter() {
        return ((exchange, chain) -> {
            logger.info("Second global pre filter invoked");

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                logger.info("Second global post filter invoked");
            }));
        });
    }

    @Bean
    @Order(2)
    public GlobalFilter thirdFilter() {
        return ((exchange, chain) -> {
            logger.info("Third global pre filter invoked");

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                logger.info("Third global post filter invoked");
            }));
        });
    }
}
