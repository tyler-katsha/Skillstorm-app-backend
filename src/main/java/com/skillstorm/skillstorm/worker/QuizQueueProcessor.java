package com.skillstorm.skillstorm.worker;

import com.skillstorm.skillstorm.dto.payloads.QuizCreatePayload;
import org.springframework.stereotype.Component;

@Component
public class QuizQueueProcessor extends QueueProcessor<QuizCreatePayload> {

    public QuizQueueProcessor(QuizWorker worker) {
        super(worker);
    }
}
