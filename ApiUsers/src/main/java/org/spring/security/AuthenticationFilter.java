package org.spring.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.spring.service.UsersService;
import org.spring.ui.request.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private Environment environment;
    private UsersService usersService;

    @Autowired
    public AuthenticationFilter(Environment environment,
                                UsersService usersService,
                                AuthenticationManager authenticationManager) {
        this.environment = environment;
        this.usersService = usersService;
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            var credentials = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequest.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getEmail(),
                            credentials.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        var userName = ((User) authResult.getPrincipal()).getUsername();
        var userDetails = usersService.getUserDetailsByEmail(userName);

        var token = JWT.create()
                .withSubject(userDetails.getEmail())
                .withExpiresAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
                        .plusMillis(Long.parseLong(
                                Objects.requireNonNull(environment.getProperty("token.expiration-time"))))))
                .sign(Algorithm.HMAC512(
                        Objects.requireNonNull(environment.getProperty("token.secret"))));

        response.addHeader("token", token);
        response.addHeader("email", userDetails.getEmail());
    }
}
