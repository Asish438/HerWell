package com.HerWell.example.Service;

import com.HerWell.example.Data.SymptomRecord;
import com.HerWell.example.Reposistry.SymptomRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class SymptomsProductService {

    private final WebClient webClient;
    private final SymptomRecordRepository symptomRecordRepository;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public SymptomsProductService(WebClient.Builder webClientBuilder, SymptomRecordRepository repo) {
        this.webClient = webClientBuilder.build();
        this.symptomRecordRepository = repo;
    }

    public SymptomRecord getSafetyProducts(List<String> symptoms) {
        try {
            String prompt = "User has reported these symptoms: " + String.join(", ", symptoms) + ".\n" +
                    "Suggest 4-5 relevant SAFETY PRODUCTS the user should use.\n" +
                    "For each product, give:\n" +
                    "- name\n" +
                    "- benefits\n" +
                    "- why to use\n" +
                    "Format response as:\n" +
                    "{\n" +
                    "  \"message\": \"As per your symptoms report, we suggest you to use these safety products\",\n" +
                    "  \"products\": [\n" +
                    "     {\"name\": \"...\", \"benefits\": \"...\", \"whyUse\": \"...\"},\n" +
                    "     {\"name\": \"...\", \"benefits\": \"...\", \"whyUse\": \"...\"},\n" +
                    "     {\"name\": \"...\", \"benefits\": \"...\", \"whyUse\": \"...\"},\n" +
                    "     {\"name\": \"...\", \"benefits\": \"...\", \"whyUse\": \"...\"}\n" +
                    "  ]\n" +
                    "}";

            String aiResponse = webClient.post()
                    .uri(geminiApiUrl + geminiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("{\"contents\":[{\"parts\":[{\"text\":\"" + prompt + "\"}]}]}")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            SymptomRecord record = new SymptomRecord();
            record.setSymptoms(symptoms);
            record.setGeminiResponse(aiResponse != null ? aiResponse : "No response from Gemini.");
            return symptomRecordRepository.save(record);

        } catch (Exception e) {
            SymptomRecord record = new SymptomRecord();
            record.setSymptoms(symptoms);
            record.setGeminiResponse("Error calling Gemini: " + e.getMessage());
            return symptomRecordRepository.save(record);
        }
    }
}
