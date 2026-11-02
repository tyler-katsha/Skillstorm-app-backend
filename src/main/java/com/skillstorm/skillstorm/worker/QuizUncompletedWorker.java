package com.skillstorm.skillstorm.worker;

import com.skillstorm.skillstorm.dto.payloads.QuizUncompletedPayload;
import com.skillstorm.skillstorm.service.UserTrackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizUncompletedWorker implements BackgroundWorker<QuizUncompletedPayload>{

    private UserTrackerService userTrackerService;

    @Override
    public void process(QuizUncompletedPayload val) {
        userTrackerService.saveUncompletedQuiz(val);
    }
}
