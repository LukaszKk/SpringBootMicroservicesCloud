package org.spring.service;

import org.spring.db.dao.UserRepository;
import org.spring.db.dto.UserEntity;
import org.spring.ui.request.UserRequest;
import org.spring.ui.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsersServiceImpl implements UsersService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UsersServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserResponse createUser(UserRequest userDetails) {
        UserEntity entity = new UserEntity();
        entity.setFirstName(userDetails.getFirstName());
        entity.setLastName(userDetails.getLastName());
        entity.setEmail(userDetails.getEmail());

        String encodedPassword = bCryptPasswordEncoder.encode(userDetails.getPassword());
        entity.setEncryptedPassword(encodedPassword);

        userRepository.save(entity);

        UserResponse response = new UserResponse();
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setEmail(entity.getEmail());
        response.setTimeStamp(LocalDateTime.now());
        return response;
    }
}
