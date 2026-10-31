package com.skillstorm.skillstorm.worker;

import com.skillstorm.skillstorm.dto.payloads.QuizUpdatePayload;
import org.springframework.stereotype.Component;

@Component
public class QuizUpdateQueueProcessor extends QueueProcessor<QuizUpdatePayload> {

    public QuizUpdateQueueProcessor(QuizUpdateWorker worker) {
        super(worker);
    }
}
