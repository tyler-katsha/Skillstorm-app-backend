package com.skillstorm.skillstorm.service;

import java.util.List;

import com.skillstorm.skillstorm.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.dto.UserDTO;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public User create(User user) {
        // TODO: Implement Hashing to store the passwords
        return userRepository.save(user);
    }
    @Cacheable(cacheNames = "user",key="#userId")
    public UserDTO getById(int userId) {
        return userMapper.mapToDto(userRepository.findById(userId).orElseThrow());
    }
    @Cacheable(cacheNames = "usernameUsers",key="#username")
    public UserDTO getUserByUsername(String username) {
        return userMapper.mapToDto(userRepository.findByUsername(username));
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
    public User update(User user) {
        return userRepository.save(user);
    }
    @Caching(evict = {
            @CacheEvict(cacheNames = "user",key="#userId"),
            @CacheEvict(cacheNames = "user",key="'all'")
    })
    public void delete(int userId) {
        userRepository.deleteById(userId);
    }
}
