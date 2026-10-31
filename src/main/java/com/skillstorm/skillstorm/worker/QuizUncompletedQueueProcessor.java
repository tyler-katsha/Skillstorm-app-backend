package com.skillstorm.skillstorm.worker;

import com.skillstorm.skillstorm.dto.payloads.QuizUncompletedPayload;
import org.springframework.stereotype.Component;

@Component
public class QuizUncompletedQueueProcessor extends QueueProcessor<QuizUncompletedPayload> {

    public QuizUncompletedQueueProcessor(QuizUncompletedWorker worker) {
        super(worker);
    }
}
