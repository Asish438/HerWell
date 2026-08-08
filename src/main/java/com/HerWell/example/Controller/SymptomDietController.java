package com.HerWell.example.Controller;

import com.HerWell.example.Data.SymptomRecord;
import com.HerWell.example.Service.SymptomDietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/symptoms")
public class SymptomDietController {

    @Autowired
    private SymptomDietService service;

    // Generate diet plan and save to DB, then return the full record
    @PostMapping("/diet-plan")
    public SymptomRecord getDietPlan(@RequestBody List<String> symptoms) {
        return service.generateAndSaveDietPlan(symptoms);
    }

    // Get all saved history
    @GetMapping("/history")
    public List<SymptomRecord> history() {
        return service.getAllHistory();
    }
}
