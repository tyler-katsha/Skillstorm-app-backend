package com.skillstorm.skillstorm.worker;

import com.skillstorm.skillstorm.dto.payloads.QuizUpdatePayload;
import com.skillstorm.skillstorm.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizUpdateWorker implements BackgroundWorker<QuizUpdatePayload> {

    private final QuizService quizService;
    @Override
    public void process(QuizUpdatePayload val) {
        quizService.update(val);
    }
}
