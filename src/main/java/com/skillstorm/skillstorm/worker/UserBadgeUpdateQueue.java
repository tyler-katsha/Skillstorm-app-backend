package com.skillstorm.skillstorm.worker;

import org.springframework.stereotype.Component;

@Component
public class UserBadgeUpdateQueue extends QueueProcessor<Void>{
    public UserBadgeUpdateQueue(UserBadgeUpdateWorker worker) {
        super(worker);
    }
}
