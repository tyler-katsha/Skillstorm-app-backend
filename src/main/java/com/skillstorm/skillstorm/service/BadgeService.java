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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillstorm.skillstorm.model.Badge;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.BadgeRepository;
import com.skillstorm.skillstorm.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
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
    public void update(Integer id, BadgeRequest request) {
        UserBadge existingUserBadge = userBadgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserBadge not found id: " + id));

        int badgeId = existingUserBadge.getBadge().getId();

        Badge existingBadge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found id: " + badgeId));

        existingBadge.setDefinitionId(request.getDefinitionId());
        existingBadge.setTitle(request.getTitle());
        existingBadge.setDescription(request.getDescription());
        existingBadge.setIcon(request.getIcon());
        existingBadge.setKind(request.getKind());
        existingBadge.setGoal(request.getGoal());


        badgeRepository.save(existingBadge);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "badge", allEntries = true)
    })
    @Transactional
    public void delete(Integer id) {

        UserBadge userBadge = userBadgeRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("UserBadge not found: " + id));

        Integer badgeId = userBadge.getBadge().getId();

        userBadgeRepository.deleteByBadgeId(id);

        badgeRepository.deleteById(badgeId);
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

    public int findGoalByDefinitionId(@Param("definitionId") String definitionId) {
        return badgeRepository.findGoalByDefinitionId(definitionId);
    }
}