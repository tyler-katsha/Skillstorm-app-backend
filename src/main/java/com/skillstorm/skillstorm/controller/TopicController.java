package com.skillstorm.skillstorm.controller;

import java.util.List;

import com.skillstorm.skillstorm.service.InspectionCacheService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.skillstorm.model.Topic;
import com.skillstorm.skillstorm.service.TopicService;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;
    private final InspectionCacheService inspectionCacheService;

    public TopicController(TopicService topicService,InspectionCacheService inspectionCacheService) {
        this.topicService = topicService;
        this.inspectionCacheService = inspectionCacheService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Topic create(@RequestBody Topic topic) {
        return topicService.create(topic);
    }

    @GetMapping("/{id}")
    public Topic getById(@PathVariable Integer id) {
        return topicService.getById(id);
    }

    @GetMapping
    public List<Topic> getAll() {
        return topicService.getAll();
    }

    @PutMapping("/{id}")
    public Topic update(@PathVariable Integer id, @RequestBody Topic updated) {
        return topicService.update(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        topicService.delete(id);
    }

    @GetMapping("/cache")
    public ResponseEntity<String> cache(){
        inspectionCacheService.inspectCache("topic");
        return ResponseEntity.ok("Cache was checked");
    }
}
