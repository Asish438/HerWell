package com.HerWell.example.Controller;

import com.HerWell.example.Service.BlogService;
import com.HerWell.example.Service.BlogService.BlogSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin(origins = "*")
public class BlogController {

    @Autowired
    private BlogService blogService;

    // POST: Fetch dynamic blogs using Inner DTO
    @PostMapping(value = "/ai-fetch", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAiBlogs(@RequestBody BlogSearchRequest request) {
        List<String> symptoms = request.getSymptoms() != null ? request.getSymptoms() : List.of();
        String jsonResult = blogService.generateDynamicBlogs(symptoms, request.getCustomQuery());
        return ResponseEntity.ok(jsonResult);
    }
}