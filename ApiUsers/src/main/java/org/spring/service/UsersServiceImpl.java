package org.spring.service;

import org.apache.catalina.User;
import org.spring.db.dao.UserRepository;
import org.spring.db.dto.UserEntity;
import org.spring.ui.request.UserRequest;
import org.spring.ui.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {

    private UserRepository userRepository;

    @Autowired
    public UsersServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse createUser(UserRequest userDetails) {
        UserEntity entity = new UserEntity();
        entity.setFirstName(userDetails.getFirstName());
        entity.setLastName(userDetails.getLastName());
        entity.setEmail(userDetails.getEmail());
        entity.setEncryptedPassword(userDetails.getPassword());

        userRepository.save(entity);

        UserResponse response = new UserResponse();
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setEmail(entity.getEmail());
        return response;
    }
}
