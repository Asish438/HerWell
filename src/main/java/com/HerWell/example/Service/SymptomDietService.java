package com.HerWell.example.Service;

import com.HerWell.example.Data.SymptomRecord;
import com.HerWell.example.Reposistry.SymptomRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SymptomDietService {

    private final WebClient webClient;
    private final SymptomRecordRepository repository;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    public SymptomDietService(WebClient.Builder webClientBuilder,
                              SymptomRecordRepository repository) {
        this.webClient = webClientBuilder.build();
        this.repository = repository;
    }

    public SymptomRecord generateAndSaveDietPlan(List<String> symptoms) {
        String symptomText = String.join(", ", symptoms);

        String prompt = "User symptoms: " + symptomText + "\n\n" +
                "👉 Based on these symptoms, create a structured personalized diet plan.\n" +
                "Format response clearly in this structure:\n\n" +
                "📝 Personalized Diet Plan (from HerWell Recommendation)\n\n" +
                "🍽 Breakfast:\n- Item 1\n- Item 2\n\n" +
                "🥗 Lunch:\n- Item 1\n- Item 2\n\n" +
                "🍎 Snacks:\n- Item 1\n- Item 2\n\n" +
                "🥘 Dinner:\n- Item 1\n- Item 2\n\n" +
                "💡 Tips:\n- Point 1\n- Point 2\n\n" +
                "⚠ Notes:\n- Point 1\n\n" +
                "📅 Generated At: (current time)\n\n" +
                "Make sure it is easy to read, uses emojis, and explains in simple words.";

        String responseFromGemini = webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"contents\":[{\"parts\":[{\"text\":\"" + prompt + "\"}]}]}")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Save in DB
        SymptomRecord record = new SymptomRecord();
        record.setSymptoms(symptoms);
        record.setGeminiResponse(responseFromGemini != null ? responseFromGemini : "No response from Gemini");
        // createdAt defaults to now
        return repository.save(record);
    }

    public List<SymptomRecord> getAllHistory() {
        return repository.findAll();
    }
}
