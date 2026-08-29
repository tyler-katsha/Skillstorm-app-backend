package com.skillstorm.skillstorm.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.google.common.annotations.Beta;
import com.google.common.hash.BloomFilter;
import com.skillstorm.skillstorm.dto.UserLogin;
import com.skillstorm.skillstorm.dto.UserRegister;
import com.skillstorm.skillstorm.dto.UserResponse;
import com.skillstorm.skillstorm.enums.Role;
import com.skillstorm.skillstorm.exceptions.AuthorizationException;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.exceptions.UsernameTakenException;
import com.skillstorm.skillstorm.jwts.JwtTokenProvider;
import com.skillstorm.skillstorm.mappers.UserMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.dto.UserDTO;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider tokenProvider;
    @Beta
    private final BloomFilter<String> filter;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, JwtTokenProvider tokenProvider,BloomFilter<String> filter) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.tokenProvider = tokenProvider;
        this.filter = filter;
    }

    @PostConstruct
    public void init(){
        List<String> existingUsername = userRepository.findAllUsername();
        existingUsername.forEach(filter::put);
    }

    public String register(UserRegister request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if(existingUser.isPresent()){
            throw new AuthorizationException(String.format("Existing user with email: %s already exist",request.getEmail()));
        }

        User user = userMapper.toUser(request);

        if(!isUsernameTaken(user.getUsername())){
            throw new UsernameTakenException(user.getUsername() + " already exist");
        }
        // hashes the password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        filter.put(user.getUsername());

        return "Account created Successfully";
    }

    public String login(UserLogin request){

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthorizationException("Invalid Credentials"));

        // check if the hash matches with the one entered and in the db password
        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new AuthorizationException("Invalid Credentials");
        }


        // generate jwt token and sends it to frontend
        return tokenProvider.generateToken(user);
    }

    @Cacheable(cacheNames = "user",key="#userId")
    public UserResponse getById(int userId) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist"));

        return userMapper.mapToResponse(existingUser);
    }
    @Cacheable(cacheNames = "emailUsers",key="#email")
    public UserDTO findByEmail(String email) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist"));

        return userMapper.mapToDto(existingUser);
    }
    @Cacheable(cacheNames = "user",key="'all'")
    public List<UserDTO> getAll() {
        return userRepository
            .findAll()
            .stream()
            .map(userMapper::mapToDto)
            .toList();
    }

    @Cacheable(cacheNames = "user",key="'top10'")
    public List<User> findTop10ByOrderByXpDesc(){
        return userRepository.findTop10ByOrderByXpDesc();
    }

    @CachePut(cacheNames = "user",key="#user.userId")
    @Caching(evict = {
            @CacheEvict(cacheNames = "user",key="#user.userId"),
            @CacheEvict(cacheNames = "usernameUsers",key="#user.username"),
            @CacheEvict(cacheNames = "user",key="'all'")
    })
    @Transactional
    public User update(User user) {
        return userRepository.save(user);
    }
    @Caching(evict = {
            @CacheEvict(cacheNames = "user",key="#userId"),
            @CacheEvict(cacheNames = "user",key="'all'")
    })

    @Transactional
    public void delete(int userId) {
        userRepository.deleteById(userId);
    }

    public boolean isUsernameTaken(String username){
        if(!filter.mightContain(username)){
            return false;
        }

        return userRepository.existsByUsername(username);
    }
}
