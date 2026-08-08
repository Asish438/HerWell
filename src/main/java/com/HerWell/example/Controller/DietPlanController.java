package com.HerWell.example.Controller;

import com.HerWell.example.Data.DietRequest;
import com.HerWell.example.Service.DietPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/diet")
@CrossOrigin(origins = "*")
public class DietPlanController {

    @Autowired
    private DietPlanService dietPlanService;

    // POST: Generate Full Diet Plan
    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> getDietPlan(@RequestBody DietRequest request) {
        String plan = dietPlanService.generateDietPlan(request);
        return ResponseEntity.ok(Map.of("plan", plan));
    }

    // GET: Search Food/Recipe Nutritional Info
    @GetMapping("/search")
    public ResponseEntity<Map<String, String>> searchFood(@RequestParam String query) {
        String result = dietPlanService.searchFoodNutrition(query);
        return ResponseEntity.ok(Map.of("result", result));
    }
}