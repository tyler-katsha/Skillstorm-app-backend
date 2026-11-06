package com.skillstorm.skillstorm.controller;

import java.time.temporal.ChronoUnit;
import java.util.List;

import com.skillstorm.skillstorm.annotations.RateLimited;
import com.skillstorm.skillstorm.dto.requests.BadgeRequest;
import com.skillstorm.skillstorm.dto.responses.BadgeResponse;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.oauth.UserPrincipal;
import com.skillstorm.skillstorm.service.InspectionCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.skillstorm.model.Badge;
import com.skillstorm.skillstorm.service.BadgeService;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;
    private final InspectionCacheService inspectionCacheService;

    @PostMapping
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<?> create(@RequestBody BadgeRequest request) {
        try{
            badgeService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch(ResourceNotFoundException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @RateLimited(capacity = 100, tokens = 100, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public List<BadgeResponse> getAll() {
        return badgeService.getAll();
    }

    @GetMapping("/user")
    @RateLimited(capacity = 100, tokens = 100, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<?> findBadgeByUser(@AuthenticationPrincipal UserPrincipal principal) {

        try {
            return ResponseEntity.ok(badgeService.findBadgesByUser(principal.getUserId()));
        } catch(ResourceNotFoundException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @RateLimited(capacity = 20, tokens = 20, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody BadgeRequest updated) {
        try {

            badgeService.update(id, updated);
            return ResponseEntity.ok().build();
        } catch(ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @RateLimited(capacity = 100, tokens = 100, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try{
            badgeService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(ResourceNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cache")
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cache(){
        var result = inspectionCacheService.inspectCache("badge");
        return ResponseEntity.ok(result + "\nCache was checked");
    }

    @PostMapping("/batch")
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    public ResponseEntity<String> batchAdd(@RequestBody List<BadgeRequest> requests) {
        try{
            badgeService.batchAdd(requests);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch(ResourceNotFoundException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/next")
    @RateLimited(capacity = 20, tokens = 20, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<BadgeResponse> findNextBadgeToUnlock(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(badgeService.findNextBadgeToUnlock(principal.getUserId()));
    }
}
