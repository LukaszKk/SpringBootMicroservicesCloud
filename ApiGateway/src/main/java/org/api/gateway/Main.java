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
    public RouteLocator routeLocator(RouteLocatorBuilder builder,
                                     AuthorizationHeaderGatewayFilter authorizationHeaderFilter) {

        return builder
            .routes()
            .route(r -> r
                .path("/apiusers/users/**")
                .and()
                .method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
                .and()
                .header("Authorization", "Bearer (.*)")
                .filters(f -> f
                    .removeRequestHeader("Cookie")
                    .rewritePath("/apiusers/(?<segment>.*)", "/$\\{segment}")
                    .filter(authorizationHeaderFilter.apply(new AuthorizationHeaderGatewayFilter.Config())))
                .uri("lb://apiusers"))
            .route(r -> r
                .path("/apiusers/users")
                .and()
                .method(HttpMethod.POST)
                .filters(f -> f
                    .removeRequestHeader("Cookie")
                    .rewritePath("/apiusers/(?<segment>.*)", "/$\\{segment}"))
                .uri("lb://apiusers"))
            .route(r -> r
                .path("/apiusers/users/login")
                .and()
                .method(HttpMethod.POST)
                .filters(f -> f
                    .removeRequestHeader("Cookie")
                    .rewritePath("/apiusers/(?<segment>.*)", "/$\\{segment}"))
                .uri("lb://apiusers"))
//            .route(r -> r
//                .path("/**")
//                .and()
//                .method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
//                .filters(f -> f
//                    .removeRequestHeader("Cookie")
//                    .rewritePath("/apiusers/(?<segment>.*)", "/$\\{segment}"))
//                .uri("lb://apiaccountmanagement"))
            .route(r -> r
                .path("/apiusers/actuator/**")
                .and()
                .method(HttpMethod.GET)
                .filters(f -> f
                    .removeRequestHeader("Cookie")
                    .rewritePath("/apiusers/(?<segment>.*)", "/$\\{segment}"))
                .uri("lb://apiusers"))
            .build();
    }
}
