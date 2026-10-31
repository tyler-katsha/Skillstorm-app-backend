package com.skillstorm.skillstorm.dto.payloads;

import com.skillstorm.skillstorm.dto.requests.CreateQuizRequest;
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
