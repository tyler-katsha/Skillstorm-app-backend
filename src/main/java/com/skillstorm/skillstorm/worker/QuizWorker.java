package com.skillstorm.skillstorm.worker;

import com.skillstorm.skillstorm.dto.payloads.QuizCreatePayload;
import com.skillstorm.skillstorm.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizWorker implements BackgroundWorker<QuizCreatePayload> {

    private final QuizService quizService;


    @Override
    public void process(QuizCreatePayload val) {
        quizService.create(val);
    }
}
