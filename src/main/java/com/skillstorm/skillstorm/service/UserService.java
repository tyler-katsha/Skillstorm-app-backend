package com.skillstorm.skillstorm.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public User getById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User update(Integer id, User updated) {
        User existing = getById(id);

        existing.setUsername(updated.getUsername());
        existing.setPassword(updated.getPassword());
        existing.setExperience(updated.getExperience());
        existing.setLevel(updated.getLevel());

        return userRepository.save(existing);
    }

    public void delete(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }
}
