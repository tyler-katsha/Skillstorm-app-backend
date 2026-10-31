package com.skillstorm.skillstorm.dto;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmallQuizResponse {
    private String topicName;
    private String difficulty;
    private Long numOfQuizForTopic;
}
