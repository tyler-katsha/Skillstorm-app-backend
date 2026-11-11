package com.skillstorm.skillstorm.controller;

import java.time.temporal.ChronoUnit;
import java.util.List;

import com.skillstorm.skillstorm.annotations.RateLimited;
import com.skillstorm.skillstorm.dto.extraDtos.UncompletedDto;
import com.skillstorm.skillstorm.dto.payloads.QuizUncompletedPayload;
import com.skillstorm.skillstorm.dto.requests.UserUpdateRequest;
import com.skillstorm.skillstorm.dto.responses.ProfileTrackerAPIResponse;
import com.skillstorm.skillstorm.dto.responses.UserResponse;
import com.skillstorm.skillstorm.exceptions.InvalidUsernameException;
import com.skillstorm.skillstorm.exceptions.RateLimitExceededException;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.exceptions.UsernameTakenException;
import com.skillstorm.skillstorm.oauth.UserPrincipal;
import com.skillstorm.skillstorm.service.InspectionCacheService;
import com.skillstorm.skillstorm.worker.QuizUncompletedQueueProcessor;
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

import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final InspectionCacheService inspectionCacheService;

    @GetMapping
    @RateLimited(capacity = 100, tokens = 100, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(userService.getAll());
        } catch(ResourceNotFoundException e){
            return new ResponseEntity<>("Resource not found",HttpStatus.NOT_FOUND);
        } catch (RateLimitExceededException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.TOO_MANY_REQUESTS);
        } catch(Exception e){
            return new ResponseEntity<>("Something went wrong. Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // UserPrincipal is fetching the logged-in user id from the JWT Token.
    @GetMapping("/me")
    @RateLimited(capacity = 30,tokens = 30,duration = 10,unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<UserResponse> getLoggedInUser(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getById(principal.getUserId()));
    }



    @PutMapping
    @RateLimited(unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<?> update(@AuthenticationPrincipal UserPrincipal principal, @RequestBody UserUpdateRequest request) {
        try {
            return ResponseEntity.ok(userService.update(principal.getUserId(),request));
        } catch(ResourceNotFoundException e){
            return new ResponseEntity<>("Resource not found",HttpStatus.NOT_FOUND);
        } catch (RateLimitExceededException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.TOO_MANY_REQUESTS);
        } catch(UsernameTakenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(InvalidUsernameException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        } catch(Exception e){
            return new ResponseEntity<>("Something went wrong. Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    @RateLimited(unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<String> delete(@AuthenticationPrincipal UserPrincipal principal) {
        try {
            userService.delete(principal.getUserId());
            return ResponseEntity.ok("Account has been removed successfully");
        } catch(ResourceNotFoundException e){
            return new ResponseEntity<>("Resource not found",HttpStatus.NOT_FOUND);
        } catch (RateLimitExceededException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.TOO_MANY_REQUESTS);
        } catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong. Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cache")
    @RateLimited(unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cache(){
        var result = inspectionCacheService.inspectCache("user");
        return ResponseEntity.ok(result + "\nCache was checked");
    }

    @GetMapping("/history")
    @RateLimited(capacity = 100, tokens = 100, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<ProfileTrackerAPIResponse> history(@AuthenticationPrincipal UserPrincipal principal) {
        try {
            return ResponseEntity.ok(userService.history(principal.getUserId()));
        } catch(ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch(RateLimitExceededException e) {
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
