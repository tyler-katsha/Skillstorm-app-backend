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
    private final BadgeRepository badgeRepository;

    @RateLimited(capacity = 5,tokens = 5,unit = ChronoUnit.MINUTES)
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegister request){
        try{
            return ResponseEntity.ok(userService.register(request));
        } catch (UsernameTakenException | AuthorizationException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
        }catch (InvalidEmailException e){
            return new ResponseEntity<>("Email domain doesn't exist",HttpStatus.BAD_REQUEST);
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>("Resource not found",HttpStatus.NOT_FOUND);
        } catch (InvalidUsernameException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>("Something went wrong. Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RateLimited(capacity = 5,tokens = 5,unit = ChronoUnit.MINUTES)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLogin request){
        try{
            return ResponseEntity.ok(userService.login(request));
        } catch(ResourceNotFoundException e){
            return new ResponseEntity<>("Resource not found",HttpStatus.NOT_FOUND);
        } catch(Exception e){
            return new ResponseEntity<>("Something went wrong. Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RateLimited(capacity = 5,tokens = 5,unit = ChronoUnit.MINUTES)
    @PostMapping("/bloom")
    public ResponseEntity<?> bloomFilter(@RequestBody BloomUser bloom){

        try{
            var type = FilterType.valueOf(bloom.getType());
            return ResponseEntity.ok(userService.takenType(type,bloom.getValue()));
        } catch(Exception e){
            return new ResponseEntity<>("Internal Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RateLimited(capacity = 5,tokens = 5,unit = ChronoUnit.MINUTES)
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPassword payload) {
        try {
            userService.forgotPassword(payload);
            return ResponseEntity.ok().build();
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
