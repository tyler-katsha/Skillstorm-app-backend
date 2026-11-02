package com.skillstorm.skillstorm.repository;

import com.skillstorm.skillstorm.dto.responses.SmallQuizResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.skillstorm.model.Topic;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    Optional<Topic> findByNameIgnoreCase(String name);

    @Query(value = """
        SELECT 
            t.name AS topicName, 
            sub.difficulty AS difficulty, 
            sub.numOfQuizForTopic AS numOfQuizForTopic
        FROM topic t
        INNER JOIN (
            SELECT 
                tq.topic_id, 
                q.difficulty, 
                COUNT(tq.quiz_id) AS numOfQuizForTopic
            FROM topic_quiz tq
            JOIN quiz q ON tq.quiz_id = q.quiz_id
            GROUP BY tq.topic_id, q.difficulty
        ) sub ON t.topic_id = sub.topic_id
        ORDER BY sub.numOfQuizForTopic DESC
        LIMIT 4
        """, nativeQuery = true)
    List<SmallQuizResponse> findTopTopicsWithQuizCountAndDifficulty();
}
