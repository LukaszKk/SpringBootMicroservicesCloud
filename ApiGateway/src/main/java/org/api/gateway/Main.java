package org.api.gateway;

import org.api.gateway.security.AuthorizationHeaderGatewayFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

@SpringBootApplication
@EnableDiscoveryClient
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder rlb,
                                     AuthorizationHeaderGatewayFilter authorizationHeaderFilter) {

        return rlb
                .routes()
                .route(r -> r
                                .method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
                                .and()
                                .path("/apiusers/users/**")
//                                .and()
//                                .header("Authorization", "Bearer (.*)")
                                .filters(f -> f.removeRequestHeader("Cookie")
//                                .rewritePath("/users-ws/(?<segment>.*)", "/$\\{segment}")
                                        .filter(authorizationHeaderFilter.apply(new
                                                AuthorizationHeaderGatewayFilter.Config())))
                                .uri("lb://apiusers")
                )
                .build();
    }
}
