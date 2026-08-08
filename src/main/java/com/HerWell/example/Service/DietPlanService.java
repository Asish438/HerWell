package com.HerWell.example.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.HerWell.example.Data.DietRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
public class DietPlanService {

    @Autowired
    private BudgetGuardService budgetGuardService;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKeyPrimary;

    @Value("${gemini.api.key.secondary:}")
    private String geminiApiKeySecondary;

    public DietPlanService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    // 1️⃣ Generate Detailed Diet Plan
    public String generateDietPlan(DietRequest req) {
        String medicalConditionsStr = (req.getMedicalConditions() != null && !req.getMedicalConditions().isEmpty())
                ? String.join(", ", req.getMedicalConditions()) : "General Health Maintenance";

        String allergiesStr = (req.getAllergies() != null && !req.getAllergies().isEmpty())
                ? String.join(", ", req.getAllergies()) : "None";

        String prompt = "You are HerWell AI, a Senior Clinical Nutritionist specializing in Female Health.\n"
                + "Generate a 24-Hour Clinical Diet Plan based on the patient profile:\n"
                + "- Health Conditions / Issues: " + medicalConditionsStr + "\n"
                + "- Bleeding Intensity: " + (req.getBleeding() != null ? req.getBleeding() : "Normal") + "\n"
                + "- Energy Level: " + (req.getEnergy() != null ? req.getEnergy() : "Moderate") + "\n"
                + "- Physical Activity: " + (req.getExercise() != null ? req.getExercise() : "Moderate") + "\n"
                + "- Primary Goal: " + (req.getGoal() != null ? req.getGoal() : "Overall Wellness") + "\n"
                + "- Dietary Preference: " + (req.getDietType() != null ? req.getDietType() : "Both Veg and Non-Veg") + "\n"
                + "- Meals Per Day: " + (req.getMealsPerDay() != null ? req.getMealsPerDay() : "3") + "\n"
                + "- Allergies / Exclusions: " + allergiesStr + "\n\n"
                + "Format the output using Markdown with EXACT headers:\n\n"
                + "### 📋 NUTRITIONAL OVERVIEW & MACRO TARGETS\n"
                + "- Recommended Calorie range, Protein, Fats, Carbs, and key micronutrients.\n\n"
                + "### 🥗 VEGETARIAN MEAL PLAN\n"
                + "- Detailed Breakfast, Lunch, Snacks, and Dinner options.\n\n"
                + "### 🍗 NON-VEGETARIAN MEAL PLAN\n"
                + "- Detailed Breakfast, Lunch, Snacks, and Dinner options.\n\n"
                + "### 💡 HYDRATION & SPECIAL HERBAL TEAS / REMEDIES\n"
                + "- Specific infused water, herbal teas, or hormone balance tips.\n\n"
                + "### 🚨 FOODS & HABITS TO STRICTLY AVOID\n"
                + "- Specific trigger foods that aggravate " + medicalConditionsStr + ".";

        return executeGeminiPayload(prompt);
    }

    // 2️⃣ Search Food & Recipe Suitability
    public String searchFoodNutrition(String query) {
        String prompt = "You are HerWell AI Nutrition Expert. Analyze this query: \"" + query + "\".\n"
                + "Provide a brief clinical evaluation in Markdown:\n"
                + "### 🔍 NUTRITIONAL ANALYSIS\n"
                + "- Key benefits or risks.\n"
                + "### 🩸 SUITABILITY DURING MENSTRUAL CYCLE\n"
                + "- Recommended during period cramps, bloating, or heavy flow.\n"
                + "### 🥗 HEALTHY ALTERNATIVES / RECIPE TIPS\n"
                + "- Quick preparation advice.";

        return executeGeminiPayload(prompt);
    }

    private String executeGeminiPayload(String promptText) {
        try {
            Map<String, Object> bodyMap = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", promptText)
                            ))
                    )
            );
            String jsonBody = objectMapper.writeValueAsString(bodyMap);
            return executeGeminiJsonBody(jsonBody);
        } catch (Exception e) {
            return "Error serializing request: " + e.getMessage();
        }
    }

    private String executeGeminiJsonBody(String jsonBody) {
        if (!budgetGuardService.isBudgetSafe()) {
            return "⚠️ Monthly Budget Limit Exceeded.";
        }

        try {
            return makeGeminiCall(jsonBody, geminiApiKeyPrimary);
        } catch (WebClientResponseException.TooManyRequests e) {
            System.out.println("⚠️ HTTP 429 on Primary Key. Retrying with Secondary Key after brief delay...");

            // Wait 1.5 seconds before calling secondary key to pass burst limits
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

            if (geminiApiKeySecondary != null && !geminiApiKeySecondary.isBlank()) {
                try {
                    return makeGeminiCall(jsonBody, geminiApiKeySecondary);
                } catch (Exception ex) {
                    return "⚠️ Rate Limit Exceeded (HTTP 429): Quota limit reached. Please wait 30 seconds.";
                }
            }
            return "⚠️ Rate Limit Exceeded (HTTP 429): Please wait 30 seconds before retrying.";
        } catch (Exception e) {
            return "⚠️ Error connecting to Gemini API: " + e.getMessage();
        }
    }
    private String makeGeminiCall(String jsonBody, String apiKey) {
        DietGeminiResponse response = webClient.post()
                .uri(geminiApiUrl + "?key=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonBody)
                .retrieve()
                .bodyToMono(DietGeminiResponse.class)
                .timeout(Duration.ofSeconds(25))
                .block();

        if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
            var candidate = response.getCandidates().get(0);
            if (candidate.getContent() != null && candidate.getContent().getParts() != null && !candidate.getContent().getParts().isEmpty()) {
                budgetGuardService.incrementUsage();
                return candidate.getContent().getParts().get(0).getText();
            }
        }
        return "No valid response received from AI model.";
    }

    public static class DietGeminiResponse {
        private List<Candidate> candidates;
        public List<Candidate> getCandidates() { return candidates; }
        public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }

        public static class Candidate {
            private Content content;
            public Content getContent() { return content; }
            public void setContent(Content content) { this.content = content; }
        }

        public static class Content {
            private List<Part> parts;
            public List<Part> getParts() { return parts; }
            public void setParts(List<Part> parts) { this.parts = parts; }
        }

        public static class Part {
            private String text;
            public String getText() { return text; }
            public void setText(String text) { this.text = text; }
        }
    }
}