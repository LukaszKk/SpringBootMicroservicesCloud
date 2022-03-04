package org.api.gateway.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class AuthorizationHeaderGatewayFilter
        implements GatewayFilterFactory<AuthorizationHeaderGatewayFilter.Config> {

    @Autowired
    Environment env;

    public static class Config {
        // properties
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            var request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            var authorizationHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
            if (authorizationHeaders == null || authorizationHeaders.isEmpty()) {
                return onError(exchange, "Authorization header is corrupted", HttpStatus.UNAUTHORIZED);
            }

            var authorizationHeader = authorizationHeaders.get(0);
            var jwt = authorizationHeader.replace("Bearer", "");

            if (!isTokenValid(jwt)) {
                return onError(exchange, "JWT token is invalid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        var response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private boolean isTokenValid(String jwt) {
        var algorithm = Algorithm.HMAC512(
                Objects.requireNonNull(env.getProperty("token.secret")));
        var decodedJWT = JWT.require(algorithm).build().verify(jwt);
        var subject = decodedJWT.getSubject();
        return subject != null && !subject.isEmpty();
    }
}
