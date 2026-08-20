package com.skillstorm.skillstorm.controller;

import com.skillstorm.skillstorm.dto.UserLogin;
import com.skillstorm.skillstorm.dto.UserRegister;
import com.skillstorm.skillstorm.exceptions.AuthorizationException;
import com.skillstorm.skillstorm.exceptions.InvalidEmailException;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.service.EmailService;
import com.skillstorm.skillstorm.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService userService;
    private final EmailService emailService;

    public AuthenticationController(UserService service,EmailService emailService){
        this.userService = service;
        this.emailService = emailService;
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegister request){
        try{

            // We can perform a Bloom filter for quick lookups for future implementations
            boolean valid = emailService.hasMXRecord(request.getEmail());

            if(!valid){
                throw new InvalidEmailException("Email domain is invalid");
            }

            return ResponseEntity.ok(userService.register(request));
        } catch (InvalidEmailException e){
            return new ResponseEntity<>("Email domain doesn't exist",HttpStatus.BAD_REQUEST);
        } catch (AuthorizationException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>("Resource not found",HttpStatus.NOT_FOUND);
        } catch(Exception e){
            return new ResponseEntity<>("Something went wrong. Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLogin request){
        try{
            return ResponseEntity.ok(userService.login(request));
        } catch(ResourceNotFoundException e){
            return new ResponseEntity<>("Resource not found",HttpStatus.NOT_FOUND);
        } catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong. Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
