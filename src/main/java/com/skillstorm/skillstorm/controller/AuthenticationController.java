package com.skillstorm.skillstorm.controller;

import com.skillstorm.skillstorm.annotations.RateLimited;
import com.skillstorm.skillstorm.dto.extraDtos.BloomUser;
import com.skillstorm.skillstorm.dto.extraDtos.ForgotPassword;
import com.skillstorm.skillstorm.dto.requests.UserLogin;
import com.skillstorm.skillstorm.dto.requests.UserRegister;
import com.skillstorm.skillstorm.enums.FilterType;
import com.skillstorm.skillstorm.enums.Kind;
import com.skillstorm.skillstorm.exceptions.*;
import com.skillstorm.skillstorm.repository.BadgeRepository;
import com.skillstorm.skillstorm.repository.LeaderboardRepository;
import com.skillstorm.skillstorm.service.EmailService;
import com.skillstorm.skillstorm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @RateLimited(capacity = 5,tokens = 5,unit = ChronoUnit.MINUTES)
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegister request){
        try{
            return ResponseEntity.ok(userService.register(request));
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (UsernameTakenException | AuthorizationException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
        } catch (InvalidUsernameException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RateLimitExceededException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.TOO_MANY_REQUESTS);
        } catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RateLimited(capacity = 5,tokens = 5,unit = ChronoUnit.MINUTES)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLogin request){
        try{
            return ResponseEntity.ok(userService.login(request));
        } catch(ResourceNotFoundException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (RateLimitExceededException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.TOO_MANY_REQUESTS);
        } catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RateLimited(capacity = 5,tokens = 5,unit = ChronoUnit.MINUTES)
    @PostMapping("/bloom")
    public ResponseEntity<?> bloomFilter(@RequestBody BloomUser bloom){

        try{
            var type = FilterType.valueOf(bloom.getType());
            return ResponseEntity.ok(userService.takenType(type,bloom.getValue()));
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (RateLimitExceededException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.TOO_MANY_REQUESTS);
        } catch(Exception e){
            return new ResponseEntity<>("Internal Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RateLimited(capacity = 5,tokens = 5,unit = ChronoUnit.MINUTES)
    @PostMapping("/email")
    public ResponseEntity<Void> sendForgetEmail(@RequestParam String email) {
        try {
            userService.sendForgetEmail(email);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }  catch (RateLimitExceededException e){
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RateLimited(capacity = 5,tokens = 5,unit = ChronoUnit.MINUTES)
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPassword payload) {
        try {
            userService.forgotPassword(payload);
            return ResponseEntity.ok().build();
        } catch(PasswordResetException e){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RateLimitExceededException e){
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
