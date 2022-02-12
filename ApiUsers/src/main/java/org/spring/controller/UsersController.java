package org.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {

    private Environment environment;

    @Autowired
    public UsersController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/status")
    public String status() {
        return "Running on port " + environment.getProperty("local.server.port");
    }
}
