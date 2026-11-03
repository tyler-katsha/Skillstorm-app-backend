package com.skillstorm.skillstorm.service;

import java.util.ArrayList;
import java.util.List;

import com.skillstorm.skillstorm.dto.extraDtos.SubmissionResultDto;
import com.skillstorm.skillstorm.dto.payloads.QuizCreatePayload;
import com.skillstorm.skillstorm.dto.payloads.QuizUpdatePayload;
import com.skillstorm.skillstorm.dto.payloads.SubmissionPayload;
import com.skillstorm.skillstorm.dto.records.QuizDTO;
import com.skillstorm.skillstorm.dto.requests.UpdateQuestionRequest;
import com.skillstorm.skillstorm.enums.Difficulty;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.mappers.QuestionMapper;
import com.skillstorm.skillstorm.mappers.QuizMapper;
import com.skillstorm.skillstorm.model.*;
import com.skillstorm.skillstorm.repository.QuestionRepository;
import com.skillstorm.skillstorm.repository.QuizRepository;
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
    private final QuestionRepository questionRepository;

    @Caching(evict = {
            @CacheEvict(cacheNames = "quiz", key = "#result.quizId"),
            @CacheEvict(cacheNames = "quiz", key = "'all'")
    })
    @Transactional
    public Quiz create(QuizCreatePayload payload) {
        String email = payload.getPrincipal().getEmail();
        Quiz quiz = quizMapper.toQuiz(payload.getRequest());
        quiz.setEmail(email);

        List<Question> questions = payload.getRequest().getQuestions()
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

        List<Topic> topics = payload.getRequest().getTopicNames()
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
    public void update(QuizUpdatePayload payload) {
        Quiz existingQuiz = quizRepository.findById(payload.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz doesn't exist"));

        String email = payload.getPrincipal().getEmail();

        Difficulty difficulty = Difficulty.valueOf(payload.getRequest().getDifficulty().toUpperCase());
        existingQuiz.setTitle(payload.getRequest().getTitle());
        existingQuiz.setDifficulty(difficulty);
        existingQuiz.setEmail(email);

        if (payload.getRequest().getTopics() != null) {
            List<Topic> updatedTopics = payload.getRequest().getTopics().stream()
                    .filter(name -> name != null && !name.trim().isEmpty())
                    .map(topicService::findOrCreate)
                    .toList();

            existingQuiz.getTopics().clear();
            existingQuiz.getTopics().addAll(updatedTopics);
        }

        if (payload.getRequest().getQuestions() != null) {
            List<Question> updatedQuestions = new ArrayList<>();

            for (UpdateQuestionRequest incomingQuestion : payload.getRequest().getQuestions()) {

                if (incomingQuestion.getId() != null) {
                    // UPDATE EXISTING QUESTION
                    Question existingQuestion = existingQuiz.getQuestions()
                            .stream()
                            .filter(q -> q.getQuestionId().equals(incomingQuestion.getId()))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Question with id: " + incomingQuestion.getId() + " doesn't exist in this quiz"));

                    existingQuestion.setText(incomingQuestion.getText());
                    existingQuestion.setDescription(incomingQuestion.getDescription());

                    // Safely apply score with a fallback
                    if (incomingQuestion.getScore() != null) {
                        existingQuestion.setScore(incomingQuestion.getScore());
                    }

                    // Handle mapping the options to Answer entities
                    mergeAnswers(existingQuestion, incomingQuestion.getOptions(), incomingQuestion.getCorrectOption());

                    updatedQuestions.add(existingQuestion);

                } else {
                    // CREATE NEW QUESTION
                    Question newQuestion = new Question();
                    newQuestion.setText(incomingQuestion.getText());
                    newQuestion.setDescription(incomingQuestion.getDescription());
                    newQuestion.setScore(incomingQuestion.getScore() != null ? incomingQuestion.getScore() : 1);
                    newQuestion.setQuiz(existingQuiz);

                    // Assuming mergeAnswers can also handle creating answers for a brand new question entity
                    mergeAnswers(newQuestion, incomingQuestion.getOptions(), incomingQuestion.getCorrectOption());

                    updatedQuestions.add(newQuestion);
                }
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

    private void mergeAnswers(Question question, List<String> options, Integer correctOption) {
        if (question.getAnswers() == null) {
            question.setAnswers(new ArrayList<>());
        } else {
            // Clear old answers. Hibernate will delete them because of orphanRemoval=true
            question.getAnswers().clear();
        }

        if (options == null || options.isEmpty()) {
            return;
        }

        // Rebuild the answers from the frontend strings
        for (int i = 0; i < options.size(); i++) {
            Answer newAnswer = new Answer();
            newAnswer.setAnswerText(options.get(i));

            // If the current loop index matches the correctOption index, mark it true
            newAnswer.setCorrect(correctOption != null && correctOption.equals(i));

            newAnswer.setQuestion(question);

            question.getAnswers().add(newAnswer);
        }
    }
}