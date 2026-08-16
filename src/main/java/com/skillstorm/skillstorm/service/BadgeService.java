package com.skillstorm.skillstorm.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.Badge;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.BadgeRepository;
import com.skillstorm.skillstorm.repository.UserRepository;

@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;

    public BadgeService(BadgeRepository badgeRepository, UserRepository userRepository) {
        this.badgeRepository = badgeRepository;
        this.userRepository = userRepository;
    }

    public Badge create(String title, String description, Integer userId) {
        if (userId == null) {
            throw new NullPointerException("userId is null in call to method `Badge create(String title, String description, Integer userId)`");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> {
            return new NoSuchElementException(String.format("Could not find user by id %d.", userId));
        });
        Badge badge = new Badge();
        badge.setName(title);
        badge.setDescription(description);
        badge.setUser(user);

        return badgeRepository.save(badge);
    }

    public Badge getById(Integer id) {
        return badgeRepository.findById(id).orElseThrow(() -> {
            return new NoSuchElementException(String.format("Could not find badge by id %d.", id));
        });
    }

    public List<Badge> getAll() {
        return badgeRepository.findAll();
    }

    public Badge update(Integer id, Badge updated) {
        Badge existing = getById(id);

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setUser(updated.getUser());

        return badgeRepository.save(existing);
    }

    public void delete(Integer id) {
        if (!badgeRepository.existsById(id)) {
            throw new IllegalArgumentException("Badge not found: " + id);
        }
        badgeRepository.deleteById(id);
    }
}
