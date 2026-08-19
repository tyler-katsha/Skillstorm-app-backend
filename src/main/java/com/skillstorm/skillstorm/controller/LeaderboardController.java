package com.skillstorm.skillstorm.controller;

import java.util.List;

import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.service.InspectionCacheService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.skillstorm.model.Leaderboard;
import com.skillstorm.skillstorm.service.LeaderboardService;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final InspectionCacheService inspectionCacheService;

    public LeaderboardController(LeaderboardService leaderboardService,InspectionCacheService inspectionCacheService) {
        this.leaderboardService = leaderboardService;
        this.inspectionCacheService = inspectionCacheService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestParam int rank, @RequestParam int totalScore, @RequestParam Integer userId) {
        try{
            return ResponseEntity.ok(leaderboardService.create(rank, totalScore, userId)); // 200 HTTP STATUS
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>("Leaderboard user was not found.",HttpStatus.NOT_FOUND); // 404 HTTP STATUS
        } catch(Exception e){
            return new ResponseEntity<>("Something went wrong. Please try again",HttpStatus.INTERNAL_SERVER_ERROR); // 500 HTTP STATUS
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        try{
            return ResponseEntity.ok(leaderboardService.getById(id)); // 200 HTTP STATUS
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>("Leaderboard user was not found.",HttpStatus.NOT_FOUND); // 404 HTTP STATUS
        } catch(Exception e){
            return new ResponseEntity<>("Something went wrong. Please try again",HttpStatus.INTERNAL_SERVER_ERROR); // 500 HTTP STATUS
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try{
            return ResponseEntity.ok(leaderboardService.getAll()); // 200 HTTP STATUS
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>("Leaderboard user was not found.",HttpStatus.NOT_FOUND); // 404 HTTP STATUS
        } catch(Exception e){
            return new ResponseEntity<>("Something went wrong. Please try again",HttpStatus.INTERNAL_SERVER_ERROR); // 500 HTTP STATUS
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Leaderboard updated) {
        try{
            return ResponseEntity.ok(leaderboardService.update(id, updated)); // 200 HTTP STATUS
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>("Leaderboard user was not found.",HttpStatus.NOT_FOUND); // 404 HTTP STATUS
        } catch(Exception e){
            return new ResponseEntity<>("Something went wrong. Please try again",HttpStatus.INTERNAL_SERVER_ERROR); // 500 HTTP STATUS
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        try{
            leaderboardService.delete(id);
            return ResponseEntity.ok("User was deleted successfully"); // 200 HTTP STATUS
        } catch (ResourceNotFoundException e){
            return new ResponseEntity<>("Leaderboard user was not found.",HttpStatus.NOT_FOUND); // 404 HTTP STATUS
        } catch(Exception e){
            return new ResponseEntity<>("Something went wrong. Please try again",HttpStatus.INTERNAL_SERVER_ERROR); // 500 HTTP STATUS
        }
    }

    @GetMapping("/cache")
    public ResponseEntity<String> cache(){
        inspectionCacheService.inspectCache("leaderboard");
        return ResponseEntity.ok("Cache was checked");
    }
}
