package com.skillstorm.skillstorm.controller;

import com.skillstorm.skillstorm.dto.UserTrackerRequest;
import com.skillstorm.skillstorm.dto.UserTrackerResponse;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.oauth.UserPrincipal;
import com.skillstorm.skillstorm.service.UserTrackerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-history")
public class UserTrackerController {

    private final UserTrackerService userTrackerService;

    public UserTrackerController(UserTrackerService userTrackerService){
        this.userTrackerService = userTrackerService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> createTracker(@RequestBody UserTrackerRequest request, @AuthenticationPrincipal UserPrincipal principal){
        try{
            request.setUserId(principal.getUserId());
            return ResponseEntity.ok(userTrackerService.createTracker(request));
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>("Leaderboard user was not found.", HttpStatus.NOT_FOUND); // 404 HTTP STATUS
        } catch(Exception e){
            return new ResponseEntity<>("Something went wrong. Please try again",HttpStatus.INTERNAL_SERVER_ERROR); // 500 HTTP STATUS
        }
    }


}
