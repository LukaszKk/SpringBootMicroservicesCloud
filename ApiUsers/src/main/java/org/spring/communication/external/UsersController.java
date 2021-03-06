package org.spring.communication.external;

import org.modelmapper.ModelMapper;
import org.spring.service.UsersService;
import org.spring.ui.request.UserRequest;
import org.spring.ui.response.UserResponse;
import org.spring.ui.response.UserResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {

    private Environment environment;
    private UsersService usersService;

    @Autowired
    public UsersController(Environment environment,
                           UsersService usersService) {
        this.environment = environment;
        this.usersService = usersService;
    }

    @GetMapping("/status")
    public String status() {
        return "Running on port " + environment.getProperty("local.server.port") + ", with token = " +
            environment.getProperty("token.secret");
    }

    @PostMapping(
        consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse userResponse = usersService.createUser(userRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{userId}", produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE,
    })
    public ResponseEntity<UserResponseModel> getUser(@PathVariable Integer userId) {

        UserResponse user = usersService.getUserByUserId(userId);
        UserResponseModel returnValue = new ModelMapper().map(user, UserResponseModel.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }
}
