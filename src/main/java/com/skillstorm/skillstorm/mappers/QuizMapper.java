package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.CreateQuizRequest;
import com.skillstorm.skillstorm.dto.QuizDTO;
import com.skillstorm.skillstorm.dto.QuizSummaryDto;
import com.skillstorm.skillstorm.model.Quiz;

import java.util.List;

public interface QuizMapper {

    QuizDTO mapToDto(Quiz quiz);

    QuizSummaryDto mapToSummaryDto(Quiz quiz);

    List<QuizSummaryDto> mapToSummaryDto(List<Quiz> quizzes);

    Quiz toQuiz(CreateQuizRequest request);
}