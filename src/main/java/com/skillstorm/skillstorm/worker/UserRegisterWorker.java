package com.skillstorm.skillstorm.worker;

import com.skillstorm.skillstorm.dto.requests.UserRegister;
import com.skillstorm.skillstorm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegisterWorker implements BackgroundWorker<UserRegister> {

    private final UserService userService;


    @Override
    public void process(UserRegister val) {
        userService.register(val);
    }
}
