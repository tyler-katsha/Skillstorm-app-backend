package com.skillstorm.skillstorm.controller;

import java.util.List;

import com.skillstorm.skillstorm.service.InspectionCacheService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.skillstorm.dto.UserDTO;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final InspectionCacheService inspectionCacheService;
    public UserController(UserService userService,InspectionCacheService inspectionCacheService) {
        this.userService = userService;
        this.inspectionCacheService = inspectionCacheService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    @GetMapping("/me")
    public UserDTO getLoggedInUser() {
        // TODO: Get user ID from request header
        return userService.getById(1);
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAll();
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Integer id, @RequestBody User updated) {
        return userService.update(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        userService.delete(id);
    }

    @GetMapping("/cache")
    public ResponseEntity<String> cache(){
        inspectionCacheService.inspectCache("user");
        return ResponseEntity.ok("Cache was checked");
    }
}
