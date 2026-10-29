package com.skillstorm.skillstorm.controller;

import java.time.temporal.ChronoUnit;
import java.util.List;

import com.skillstorm.skillstorm.annotations.RateLimited;
import com.skillstorm.skillstorm.dto.*;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.model.Question;
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

import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.service.QuizService;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final InspectionCacheService inspectionCacheService;

    @PostMapping
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<Quiz> create(@RequestBody CreateQuizRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        try{

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(quizService.create(request,principal));

        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public QuizDTO getById(@PathVariable Integer id) {
        return quizService.getById(id);
    }

    @GetMapping
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public List<QuizDTO> getAll() {
        return quizService.getAll();
    }

    @PutMapping("/{id}")
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody UpdateQuizRequest updatedQuiz, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {

            quizService.update(id,updatedQuiz,userPrincipal.getEmail());
            return ResponseEntity.ok().build();

        } catch(ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try{
            quizService.delete(id);
            return ResponseEntity.ok().build();
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cache")
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cache(){
        inspectionCacheService.inspectCache("quiz");
        return ResponseEntity.ok("Cache was checked");
    }


    @PostMapping("/{id}/submit")
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<SubmissionResultDto> submit(@PathVariable int id, @RequestBody SubmissionPayload payload, @AuthenticationPrincipal UserPrincipal principal) {
        try {

            var res = quizService.submit(id,payload,principal.getUserId());
            return ResponseEntity.ok(res);
        } catch(ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
