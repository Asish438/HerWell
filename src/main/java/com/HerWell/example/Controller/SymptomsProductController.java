package com.HerWell.example.Controller;

import com.HerWell.example.Data.SymptomRecord;
import com.HerWell.example.Service.SymptomsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/symptoms")
public class SymptomsProductController {

    @Autowired
    private SymptomsProductService symptomsProductService;

    // 🔹 Symptoms → Gemini → Safety Products
    @PostMapping("/safety-products")
    public SymptomRecord recommendSafetyProducts(@RequestBody List<String> symptoms) {
        return symptomsProductService.getSafetyProducts(symptoms);
    }
}
