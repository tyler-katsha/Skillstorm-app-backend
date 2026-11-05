package com.skillstorm.skillstorm.controller;

import java.time.temporal.ChronoUnit;
import java.util.List;

import com.skillstorm.skillstorm.annotations.RateLimited;
import com.skillstorm.skillstorm.dto.extraDtos.SubmissionResultDto;
import com.skillstorm.skillstorm.dto.extraDtos.UncompletedDto;
import com.skillstorm.skillstorm.dto.payloads.QuizCreatePayload;
import com.skillstorm.skillstorm.dto.payloads.QuizUncompletedPayload;
import com.skillstorm.skillstorm.dto.payloads.QuizUpdatePayload;
import com.skillstorm.skillstorm.dto.payloads.SubmissionPayload;
import com.skillstorm.skillstorm.dto.records.QuizDTO;
import com.skillstorm.skillstorm.dto.requests.CreateQuizRequest;
import com.skillstorm.skillstorm.dto.requests.UpdateQuizRequest;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.oauth.UserPrincipal;
import com.skillstorm.skillstorm.service.InspectionCacheService;
import com.skillstorm.skillstorm.worker.QuizQueueProcessor;
import com.skillstorm.skillstorm.worker.QuizUncompletedQueueProcessor;
import com.skillstorm.skillstorm.worker.QuizUpdateQueueProcessor;
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


    private final QuizQueueProcessor quizQueueProcessor;
    private final QuizUpdateQueueProcessor quizUpdateQueueProcessor;
    private final QuizUncompletedQueueProcessor uncompletedQueueProcessor;

    @PostMapping
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<Quiz> create(@RequestBody CreateQuizRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        try{

            QuizCreatePayload payload = QuizCreatePayload.builder()
                    .request(request)
                    .principal(principal)
                    .build();

            quizQueueProcessor.enqueue(payload);
            return ResponseEntity
                    .status(HttpStatus.CREATED).build();

        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/batch")
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<Void> batchCreate(@RequestBody List<CreateQuizRequest> requests, @AuthenticationPrincipal UserPrincipal principal) {
        try{

            for (CreateQuizRequest request: requests) {
                QuizCreatePayload payload = QuizCreatePayload.builder()
                        .request(request)
                        .principal(principal)
                        .build();

                quizQueueProcessor.enqueue(payload);
            }
            return ResponseEntity
                    .status(HttpStatus.CREATED).build();

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
    @RateLimited(capacity = 100, tokens = 100, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public List<QuizDTO> getAll() {
        return quizService.getAll();
    }

    @PutMapping("/{id}")
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody UpdateQuizRequest updatedQuiz, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {

            System.out.println("Updated: " + updatedQuiz);

            QuizUpdatePayload payload = QuizUpdatePayload.builder()
                    .id(id)
                    .request(updatedQuiz)
                    .principal(userPrincipal)
                    .build();

            quizUpdateQueueProcessor.enqueue(payload);

            return ResponseEntity.ok()
                    .build();

        } catch(ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
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
        var result = inspectionCacheService.inspectCache("quiz");
        return ResponseEntity.ok(result + "\nCache was checked");
    }


    @PostMapping("/{id}/submit")
    @RateLimited(capacity = 5, tokens = 5, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<SubmissionResultDto> submit(@PathVariable int id, @RequestBody SubmissionPayload payload, @AuthenticationPrincipal UserPrincipal principal) {
        try {

            return ResponseEntity.ok(quizService.submit(id,payload,principal.getUserId()));
        } catch(ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/uncompleted/{quizId}")
    @RateLimited(capacity = 100, tokens = 100, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<Void> saveUncompletedQuiz(@PathVariable int quizId, @RequestBody UncompletedDto dto, @AuthenticationPrincipal UserPrincipal principal) {
        try {

            QuizUncompletedPayload payload = QuizUncompletedPayload.builder()
                    .uncompletedDto(dto)
                    .quizId(quizId)
                    .userId(principal.getUserId())
                    .build();

            uncompletedQueueProcessor.enqueue(payload);

            return ResponseEntity.ok().build();
        } catch(ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
