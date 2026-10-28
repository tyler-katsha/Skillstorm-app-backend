package com.skillstorm.skillstorm.controller;

import com.skillstorm.skillstorm.annotations.RateLimited;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.service.InspectionCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.skillstorm.service.LeaderboardService;

import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final InspectionCacheService inspectionCacheService;

    @GetMapping
    @RateLimited(capacity = 100, tokens = 100, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<?> findAll() {
        try{
            return ResponseEntity.ok(leaderboardService.findAll()); // 200 HTTP STATUS
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>("Leaderboard user was not found.",HttpStatus.NOT_FOUND); // 404 HTTP STATUS
        } catch(Exception e){
            return new ResponseEntity<>("Something went wrong. Please try again",HttpStatus.INTERNAL_SERVER_ERROR); // 500 HTTP STATUS
        }
    }

    @GetMapping("/cache")
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cache(){
        var result = inspectionCacheService.inspectCache("leaderboard");
        return ResponseEntity.ok( result + "\nCache was checked");
    }
}
