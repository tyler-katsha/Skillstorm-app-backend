package com.skillstorm.skillstorm.worker;

import org.springframework.stereotype.Component;

@Component
public class BadgeQueueProcessor extends QueueProcessor<Integer>{

    public BadgeQueueProcessor(BadgeInitializationWorker worker) {
        super(worker);
    }
}
