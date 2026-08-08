package com.HerWell.example.Controller;

import com.HerWell.example.Data.UserQuery;
import com.HerWell.example.Service.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/query")
public class UserQueryController {

    @Autowired
    private UserQueryService queryService;

    // Submit user query
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuery(@RequestBody UserQuery userQuery) {
        try {
            UserQuery savedQuery = queryService.submitQuery(userQuery);
            return ResponseEntity.ok("Query submitted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error submitting query: " + e.getMessage());
        }
    }
}
