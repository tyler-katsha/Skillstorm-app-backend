package com.skillstorm.skillstorm.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.skillstorm.skillstorm.dto.CreateQuizRequest;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.mappers.QuestionMapper;
import com.skillstorm.skillstorm.mappers.QuizMapper;
import com.skillstorm.skillstorm.model.Answer;
import com.skillstorm.skillstorm.model.Question;
import com.skillstorm.skillstorm.model.Topic;
import com.skillstorm.skillstorm.oauth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.dto.QuizDTO;
import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.repository.QuizRepository;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;
    private final QuestionMapper questionMapper;
    private final TopicService topicService;


    public Quiz create(CreateQuizRequest request, UserPrincipal principal) {

        String email = principal.getEmail();

        Quiz quiz = quizMapper.toQuiz(request);

        // Store the creator so only the creator or an admin can edit the quiz
        quiz.setEmail(email);

        Set<Question> questions = request.getQuestions()
                .stream()
                .map(questionRequest -> {

                    Question question = questionMapper.toQuestion(questionRequest);

                    question.setQuiz(quiz);

                    Set<Answer> answers = questionRequest.getOptions()
                            .stream()
                            .map(option -> Answer.builder()
                                    .answerText(option)
                                    .isCorrect(
                                            option.equals(
                                                    questionRequest.getOptions()
                                                            .get(questionRequest.getCorrectOption())
                                            )
                                    )
                                    .question(question)
                                    .build()
                            )
                            .collect(Collectors.toSet());

                    question.setAnswers(answers);

                    return question;
                })
                .collect(Collectors.toSet());

        quiz.setQuestions(questions);

        Set<Topic> topics = request.getTopicNames()
                .stream()
                .map(topicService::findOrCreate)
                .collect(Collectors.toSet());

        quiz.setTopics(topics);

        return quizRepository.save(quiz);
    }

    @Cacheable(cacheNames = "quiz",key="#quizId")
    public QuizDTO getById(int quizId) {

        Quiz existingQuiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz doesn't exist"));

        return quizMapper.mapToDto(existingQuiz);
    }
    @Cacheable(cacheNames = "quiz",key="'all'")
    public List<QuizDTO> getAll() {

        return quizRepository.findAll()
            .stream()
            .map(quizMapper::mapToDto)
            .toList();
    }
    @CachePut(cacheNames = "quiz",key="#quiz.quizId")
    @Caching(evict = {
            @CacheEvict(cacheNames = "quiz",key = "#quiz.quizId"),
            @CacheEvict(cacheNames = "quiz",key="'all'")
    })
    public Quiz update(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "quiz",key = "#quizId"),
            @CacheEvict(cacheNames = "quiz",key="'all'")
    })
    public void delete(int quizId) {
        quizRepository.deleteById(quizId);
    }
}
