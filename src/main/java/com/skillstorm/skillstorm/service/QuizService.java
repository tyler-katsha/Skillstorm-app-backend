package com.skillstorm.skillstorm.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.skillstorm.skillstorm.dto.*;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.mappers.QuestionMapper;
import com.skillstorm.skillstorm.mappers.QuizMapper;
import com.skillstorm.skillstorm.model.*;
import com.skillstorm.skillstorm.oauth.UserPrincipal;
import com.skillstorm.skillstorm.repository.QuizRepository;
import com.skillstorm.skillstorm.repository.UserRepository;
import com.skillstorm.skillstorm.repository.UserTrackerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;
    private final QuestionMapper questionMapper;
    private final TopicService topicService;
    private final UserTrackerService userTrackerService;

    @Caching(evict = {
            @CacheEvict(cacheNames = "quiz", key = "#result.quizId"),
            @CacheEvict(cacheNames = "quiz", key = "'all'")
    })
    @Transactional
    public Quiz create(CreateQuizRequest request, UserPrincipal principal) {
        String email = principal.getEmail();
        Quiz quiz = quizMapper.toQuiz(request);
        quiz.setEmail(email);

        List<Question> questions = request.getQuestions()
                .stream()
                .map(questionRequest -> {
                    Question question = questionMapper.toQuestion(questionRequest);
                    question.setQuiz(quiz);

                    List<Answer> answers = questionRequest.getOptions()
                            .stream()
                            .map(option -> Answer.builder()
                                    .answerText(option)
                                    .isCorrect(option.equals(questionRequest.getOptions().get(questionRequest.getCorrectOption())))
                                    .question(question)
                                    .build()
                            )
                            .toList();

                    question.setAnswers(answers);
                    return question;
                })
                .toList();

        quiz.setQuestions(questions);

        List<Topic> topics = request.getTopicNames()
                .stream()
                .map(topicService::findOrCreate)
                .toList();

        quiz.setTopics(topics);

        return quizRepository.save(quiz);
    }

    @Cacheable(cacheNames = "quiz", key = "#quizId")
    public QuizDTO getById(int quizId) {
        Quiz existingQuiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz doesn't exist"));

        return quizMapper.mapToDto(existingQuiz);
    }

    @Cacheable(cacheNames = "quiz", key = "'all'")
    public List<QuizDTO> getAll() {
        return quizRepository.findAll()
                .stream()
                .map(quizMapper::mapToDto)
                .toList();
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "quiz", key = "#id"),
            @CacheEvict(cacheNames = "quiz", key = "'all'")
    })
    @Transactional
    public void update(Integer id, UpdateQuizRequest quiz, String email) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz doesn't exist"));

        existingQuiz.setTitle(quiz.getTitle());
        existingQuiz.setDifficulty(quiz.getDifficulty());
        existingQuiz.setEmail(email);

        if (quiz.getTopics() != null) {
            List<Topic> updatedTopics = quiz.getTopics().stream()
                    .filter(name -> name != null && !name.trim().isEmpty())
                    .map(topicService::findOrCreate)
                    .toList();

            existingQuiz.getTopics().clear();
            existingQuiz.getTopics().addAll(updatedTopics);
        }

        if (quiz.getQuestions() != null) {
            List<Question> updatedQuestions = new ArrayList<>();

            for (Question incomingQuestion : quiz.getQuestions()) {
                if (incomingQuestion.getQuestionId() != null) {
                    Question existingQuestion = existingQuiz.getQuestions()
                            .stream()
                            .filter(q -> q.getQuestionId().equals(incomingQuestion.getQuestionId()))
                            .findFirst()
                            .orElse(null);

                    if (existingQuestion != null) {
                        existingQuestion.setText(incomingQuestion.getText());
                        existingQuestion.setScore(incomingQuestion.getScore());
                        mergeAnswers(existingQuestion, incomingQuestion.getAnswers());
                        updatedQuestions.add(existingQuestion);
                        continue;
                    }
                }

                incomingQuestion.setQuestionId(null);
                incomingQuestion.setQuiz(existingQuiz);

                if (incomingQuestion.getAnswers() != null) {
                    for (Answer a : incomingQuestion.getAnswers()) {
                        a.setAnswerId(null);
                        a.setQuestion(incomingQuestion);
                    }
                }
                updatedQuestions.add(incomingQuestion);
            }

            existingQuiz.getQuestions().clear();
            existingQuiz.getQuestions().addAll(updatedQuestions);
        }

        quizRepository.save(existingQuiz);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "quiz", key = "#quizId"),
            @CacheEvict(cacheNames = "quiz", key = "'all'")
    })
    @Transactional
    public void delete(int quizId) {
        quizRepository.deleteById(quizId);
    }


    @Transactional
    public SubmissionResultDto submit(int id, SubmissionPayload payload, int userId) {
        return userTrackerService.submit(id,payload,userId);
    }

    private void mergeAnswers(Question existingQuestion, List<Answer> incomingAnswers) {
        if (existingQuestion.getAnswers() == null) {
            existingQuestion.setAnswers(new ArrayList<>());
        }

        if (incomingAnswers == null || incomingAnswers.isEmpty()) {
            existingQuestion.getAnswers().clear();
            return;
        }

        List<Answer> updatedAnswers = new ArrayList<>();

        for (Answer incomingAnswer : incomingAnswers) {
            if (incomingAnswer.getAnswerId() != null) {
                Answer existingAnswer = existingQuestion.getAnswers()
                        .stream()
                        .filter(a -> a.getAnswerId().equals(incomingAnswer.getAnswerId()))
                        .findFirst()
                        .orElse(null);

                if (existingAnswer != null) {
                    existingAnswer.setAnswerText(incomingAnswer.getAnswerText());
                    existingAnswer.setCorrect(incomingAnswer.isCorrect());
                    updatedAnswers.add(existingAnswer);
                    continue;
                }
            }

            incomingAnswer.setAnswerId(null);
            incomingAnswer.setQuestion(existingQuestion);
            updatedAnswers.add(incomingAnswer);
        }

        existingQuestion.getAnswers().clear();
        existingQuestion.getAnswers().addAll(updatedAnswers);
    }
}