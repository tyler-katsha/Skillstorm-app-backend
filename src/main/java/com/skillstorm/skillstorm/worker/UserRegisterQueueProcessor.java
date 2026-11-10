package com.skillstorm.skillstorm.worker;

import com.skillstorm.skillstorm.dto.requests.UserRegister;
import org.springframework.stereotype.Component;

@Component
public class UserRegisterQueueProcessor extends QueueProcessor<UserRegister> {
    public UserRegisterQueueProcessor(UserRegisterWorker worker) {
        super(worker);
    }
}
