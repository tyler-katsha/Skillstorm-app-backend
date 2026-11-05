package com.skillstorm.skillstorm.service;

import java.util.List;

import com.skillstorm.skillstorm.dto.requests.BadgeRequest;
import com.skillstorm.skillstorm.dto.responses.BadgeResponse;
import com.skillstorm.skillstorm.dto.responses.SmallBadgeResponse;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.mappers.BadgeMapper;
import com.skillstorm.skillstorm.model.UserBadge;
import com.skillstorm.skillstorm.repository.UserBadgeRepository;
import com.skillstorm.skillstorm.worker.BadgeQueueProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.skillstorm.model.Badge;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.BadgeRepository;
import com.skillstorm.skillstorm.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final BadgeMapper badgeMapper;
    private final BadgeQueueProcessor badgeQueueProcessor;
    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;


    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "badge", key="'all'")
    })
    public void create(BadgeRequest req) {

        Badge badge = badgeMapper.toBadge(req);
        Badge savedBadge = badgeRepository.save(badge);

        // offload the heavy initialization to a background worker
        badgeQueueProcessor.enqueue(savedBadge.getId());
    }

    @Cacheable(cacheNames = "badge", key="#id")
    public Badge getById(Integer id) {
        return badgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found: " + id));
    }

    @Cacheable(cacheNames = "badge", key="'all'")
    public List<BadgeResponse> getAll() {
        return badgeRepository.findAll().stream()
                .map(badgeMapper::mapToBadgeResponse)
                .toList();
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "badge", key="'all'")
    })
    public Badge update(Integer id, BadgeRequest request) {
        Badge existing = getById(id);

        existing.setDefinitionId(request.getDefinitionId());
        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setIcon(request.getIcon());
        existing.setKind(request.getKind());
        existing.setGoal(request.getGoal());

        return badgeRepository.save(existing);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "badge", key="#id"),
            @CacheEvict(cacheNames = "badge", key="'all'")
    })
    @Transactional
    public void delete(Integer id) {
        if (!badgeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Badge not found: " + id);
        }
        userBadgeRepository.deleteByBadgeId(id);
        badgeRepository.deleteById(id);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "badge", key="'all'")
    })
    public void batchAdd(List<BadgeRequest> requests) {

        List<Badge> badges = requests.stream()
                .map(badgeMapper::toBadge)
                .toList();

        List<Badge> savedBadges = badgeRepository.saveAll(badges);

        // Producer: Queue up each new badge for background user initialization
        for (Badge savedBadge : savedBadges) {
            badgeQueueProcessor.enqueue(savedBadge.getId());
        }
    }

    public List<BadgeResponse> findBadgesByUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        List<UserBadge> userBadges = userBadgeRepository.findByUser(user);

        return userBadges.stream()
                .map(badgeMapper::mapToBadgeResponse)
                .toList();
    }

    public List<SmallBadgeResponse> findBadgesToDisplay() {
        return badgeRepository.find4BadgesToDisplay();
    }

    public BadgeResponse findNextBadgeToUnlock(int userId) {
        return badgeRepository.findNextBadgeToUnlock(userId);
    }
}