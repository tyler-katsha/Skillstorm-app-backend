package com.skillstorm.skillstorm.dto.payloads;

import com.skillstorm.skillstorm.dto.requests.UpdateQuizRequest;
import com.skillstorm.skillstorm.oauth.UserPrincipal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizUpdatePayload {
    private Integer id;
    private UpdateQuizRequest request;
    private UserPrincipal principal;
}
