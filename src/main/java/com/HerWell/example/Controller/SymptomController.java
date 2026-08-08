package com.HerWell.example.Controller;

import com.HerWell.example.Data.SymptomRecord;
import com.HerWell.example.Service.BudgetGuardService;
import com.HerWell.example.Service.SymptomService;
import com.HerWell.example.Service.SymptomService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/symptoms")
@CrossOrigin(origins = "*") // CORS Fix for frontend calls
public class SymptomController {

    @Autowired
    private SymptomService symptomService;

    @Autowired
    private BudgetGuardService budgetGuardService;

    // 1️⃣ Save raw symptoms array
    @PostMapping("/submit")
    public ResponseEntity<SymptomRecord> submitSymptoms(@RequestBody List<String> symptoms) {
        if (symptoms == null || symptoms.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        SymptomRecord savedRecord = symptomService.processSymptoms(symptoms);
        return ResponseEntity.ok(savedRecord);
    }

    // 2️⃣ Get all raw symptom entries
    @GetMapping("/all")
    public ResponseEntity<List<SymptomRecord>> getAllRecords() {
        return ResponseEntity.ok(symptomService.getAllRecords());
    }

    // 3️⃣ Get structured medical evaluation for latest submission
    @GetMapping("/elaborated")
    public ResponseEntity<List<FormattedRecord>> getElaboratedRecords() {
        return ResponseEntity.ok(symptomService.getElaboratedRecords());
    }

    // 4️⃣ AI Doctor Chat Interface
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chatWithDoctorAI(@RequestBody ChatRequest chatRequest) {
        if (chatRequest.getNewQuestion() == null || chatRequest.getNewQuestion().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ChatResponse("Question cannot be empty."));
        }
        ChatResponse response = symptomService.processChatConversation(chatRequest);
        return ResponseEntity.ok(response);
    }

    // 5️⃣ Budget & Usage Tracking Endpoint
    @GetMapping("/admin/budget-status")
    public ResponseEntity<Map<String, Object>> getBudgetStatus() {
        int usedCount = budgetGuardService.getCurrentUsageCount();
        int maxLimit = 10000;
        double estimatedCostINR = (usedCount / 10000.0) * 150.0;

        return ResponseEntity.ok(Map.of(
                "monthlyRequestsUsed", usedCount,
                "monthlyLimit", maxLimit,
                "remainingRequests", maxLimit - usedCount,
                "estimatedCostINR", String.format("%.2f INR", estimatedCostINR),
                "status", budgetGuardService.isBudgetSafe() ? "HEALTHY" : "CAP_EXCEEDED"
        ));
    }
}