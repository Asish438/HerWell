package com.HerWell.example.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DailyHealthService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public DailyHealthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    // Method to generate diet/symptoms advice
    public SymptomsDietResponse getSymptomsDiet(
            String whiteDischarge,
            String hemoglobinLevel,
            String infections,
            String skinCondition,
            String sleepProblems,
            String hairCondition,
            String mood
    ) {
        try {
            // Create a prompt for Gemini AI
            String prompt = "User daily health inputs:\n" +
                    "White discharge: " + whiteDischarge + "\n" +
                    "Hemoglobin Level: " + hemoglobinLevel + "\n" +
                    "Infections: " + infections + "\n" +
                    "Skin condition: " + skinCondition + "\n" +
                    "Sleep Problems: " + sleepProblems + "\n" +
                    "Hair condition: " + hairCondition + "\n" +
                    "Current Mood: " + mood + "\n\n" +
                    "Provide a personalized diet plan and advice based on these symptoms. Use emojis, bullet points, and simple language.";

            String aiResponse = webClient.post()
                    .uri(geminiApiUrl + geminiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("{\"contents\":[{\"parts\":[{\"text\":\"" + prompt + "\"}]}]}")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            String dietPlan = aiResponse != null ? aiResponse : "No response from Gemini.";

            // Return a frontend-friendly object
            return new SymptomsDietResponse(
                    whiteDischarge, hemoglobinLevel, infections, skinCondition,
                    sleepProblems, hairCondition, mood, dietPlan,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );

        } catch (Exception e) {
            return new SymptomsDietResponse(
                    whiteDischarge, hemoglobinLevel, infections, skinCondition,
                    sleepProblems, hairCondition, mood,
                    "Error calling Gemini: " + e.getMessage(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
        }
    }

    // DTO for frontend
    public static class SymptomsDietResponse {
        private String whiteDischarge;
        private String hemoglobinLevel;
        private String infections;
        private String skinCondition;
        private String sleepProblems;
        private String hairCondition;
        private String mood;
        private String symptomsDietPlan;
        private String generatedAt;

        public SymptomsDietResponse(String whiteDischarge, String hemoglobinLevel, String infections,
                                    String skinCondition, String sleepProblems, String hairCondition,
                                    String mood, String symptomsDietPlan, String generatedAt) {
            this.whiteDischarge = whiteDischarge;
            this.hemoglobinLevel = hemoglobinLevel;
            this.infections = infections;
            this.skinCondition = skinCondition;
            this.sleepProblems = sleepProblems;
            this.hairCondition = hairCondition;
            this.mood = mood;
            this.symptomsDietPlan = symptomsDietPlan;
            this.generatedAt = generatedAt;
        }

        public String getWhiteDischarge() { return whiteDischarge; }
        public String getHemoglobinLevel() { return hemoglobinLevel; }
        public String getInfections() { return infections; }
        public String getSkinCondition() { return skinCondition; }
        public String getSleepProblems() { return sleepProblems; }
        public String getHairCondition() { return hairCondition; }
        public String getMood() { return mood; }
        public String getSymptomsDietPlan() { return symptomsDietPlan; }
        public String getGeneratedAt() { return generatedAt; }
    }
}
