package com.skillstorm.skillstorm.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.dto.Mapper;
import com.skillstorm.skillstorm.dto.UserDTO;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Autowired
    public UserService(UserRepository userRepository, Mapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public UserDTO getById(int userId) {
        return mapper.mapToDto(userRepository.findById(userId).orElseThrow(() -> {
            return new NoSuchElementException(String.format("Could not find user by id %d.", userId));
        }));
    }

    public UserDTO getUserByUsername(String username) {
        return mapper.mapToDto(userRepository.findByUsername(username));
    }

    public List<UserDTO> getAll() {
        return userRepository
            .findAll()
            .stream()
            .map(mapper::mapToDto)
            .toList();
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public void delete(int userId) {
        userRepository.deleteById(userId);
    }
}
