package org.spring.service;

import org.spring.ui.request.UserRequest;
import org.spring.ui.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UsersService extends UserDetailsService {

    UserResponse createUser(UserRequest userDetails);
    UserResponse getUserDetailsByEmail(String email);
    UserResponse getUserByUserId(Integer userId);
}
