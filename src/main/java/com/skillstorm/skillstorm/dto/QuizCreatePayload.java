package com.skillstorm.skillstorm.dto;

import com.skillstorm.skillstorm.oauth.UserPrincipal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizCreatePayload {
    private CreateQuizRequest request;
    private UserPrincipal principal;
}
