package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.requests.CreateQuizRequest;
import com.skillstorm.skillstorm.dto.records.QuizDTO;
import com.skillstorm.skillstorm.dto.extraDtos.QuizSummaryDto;
import com.skillstorm.skillstorm.model.Quiz;

import java.util.List;

public interface QuizMapper {

    QuizDTO mapToDto(Quiz quiz);

    QuizSummaryDto mapToSummaryDto(Quiz quiz);

    List<QuizSummaryDto> mapToSummaryDto(List<Quiz> quizzes);

    Quiz toQuiz(CreateQuizRequest request);
}