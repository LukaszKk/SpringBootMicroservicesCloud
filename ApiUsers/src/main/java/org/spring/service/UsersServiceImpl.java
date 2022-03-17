package org.spring.service;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.communication.internal.AlbumsServiceClient;
import org.spring.db.dao.UserRepository;
import org.spring.db.dto.UserEntity;
import org.spring.ui.request.UserRequest;
import org.spring.ui.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class UsersServiceImpl implements UsersService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//    private RestTemplate restTemplate;
    private final Environment env;
    private final AlbumsServiceClient albumsServiceClient;

    @Autowired
    public UsersServiceImpl(UserRepository userRepository,
                            BCryptPasswordEncoder bCryptPasswordEncoder,
                            AlbumsServiceClient albumsServiceClient,
                            Environment environment) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.albumsServiceClient = albumsServiceClient;
        this.env = environment;
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
    public UserResponse getUserByUserId(Integer userId) {
        Optional<UserEntity> entity = userRepository.findById(userId);
        if (entity.isEmpty()) {
            throw new RuntimeException(userId + " not found");
        }

        var userResponse = new ModelMapper().map(entity.get(), UserResponse.class);

//        String albumsUrl = String.format(Objects.requireNonNull(env.getProperty("albums.url")), userId);
//        ResponseEntity<List<AlbumResponseModel>> albumsListResponse =
//            restTemplate.exchange(albumsUrl, HttpMethod.GET, null,
//                new ParameterizedTypeReference<>() {
//                });
//        List<AlbumResponseModel> albumsList = albumsListResponse.getBody();

        logger.info("Before calling albums Microservice");

        var albumsList = albumsServiceClient.getAlbums(userId);

        userResponse.setAlbums(albumsList);

        return userResponse;
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
