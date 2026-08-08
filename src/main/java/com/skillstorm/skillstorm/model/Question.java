package com.skillstorm.skillstorm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @Column(name = "answer")
    private String answer;

    @Column(name = "possible_answers")
    private String possibleAnswers; // stored as TEXT in schema

    @Column(name = "question_title")
    private String questionTitle;

    @Column(name = "score", nullable = false)
    private int score;

    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getPossibleAnswers() { return possibleAnswers; }
    public void setPossibleAnswers(String possibleAnswers) { this.possibleAnswers = possibleAnswers; }

    public String getQuestionTitle() { return questionTitle; }
    public void setQuestionTitle(String questionTitle) { this.questionTitle = questionTitle; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
