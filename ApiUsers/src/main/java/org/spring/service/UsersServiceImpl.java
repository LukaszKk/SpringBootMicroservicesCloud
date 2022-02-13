package org.spring.service;

import org.spring.db.dao.UserRepository;
import org.spring.db.dto.UserEntity;
import org.spring.ui.request.UserRequest;
import org.spring.ui.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
        UserEntity entity = toEntity(userDetails);

        userRepository.save(entity);

        return toResponse(entity);
    }

    @Override
    public UserResponse getUserDetailsByEmail(String email) {
        UserEntity entity = userRepository.findByEmail(email);
        if (entity == null) {
            throw new UsernameNotFoundException(email);
        }

        return toResponse(entity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByEmail(username);
        if (entity == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(entity.getEmail(), entity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
    }

    private UserResponse toResponse(UserEntity entity) {
        UserResponse response = new UserResponse();
        response.setFirstName(entity.getFirstName());
        response.setLastName(entity.getLastName());
        response.setEmail(entity.getEmail());
        response.setTimeStamp(LocalDateTime.now());
        return response;
    }

    private UserEntity toEntity(UserRequest request) {
        UserEntity entity = new UserEntity();
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setEmail(request.getEmail());

        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());
        entity.setEncryptedPassword(encodedPassword);

        return entity;
    }
}
