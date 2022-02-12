package org.spring.service;

import org.spring.ui.request.UserRequest;
import org.spring.ui.response.UserResponse;
import org.springframework.stereotype.Service;

@Service
public interface UsersService {

    UserResponse createUser(UserRequest userDetails);
}
