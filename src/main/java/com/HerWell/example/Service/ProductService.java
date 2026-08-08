package com.HerWell.example.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public ProductService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    public String generateDynamicProducts(List<String> symptoms, String customQuery) {
        try {
            String topicContext = (customQuery != null && !customQuery.trim().isEmpty())
                    ? "Requested Item/Need: " + customQuery
                    : "User Reported Symptoms: " + (symptoms != null ? String.join(", ", symptoms) : "General Wellness");

            String prompt = "You are HerWell AI, a women's health products consultant.\n"
                    + "Based on the user's situation: [" + topicContext + "], generate AT LEAST 5 highly suitable wellness and period care product recommendations.\n\n"
                    + "STRICT REQUIREMENT: Respond ONLY with a valid JSON object matching this exact structure, with NO markdown code block wrapper (no ```json):\n"
                    + "{\n"
                    + "  \"products\": [\n"
                    + "    {\n"
                    + "      \"name\": \"Pain Relief Heat Patches\",\n"
                    + "      \"category\": \"Cramp Relief\",\n"
                    + "      \"description\": \"Air-activated heat patches that provide up to 8 hours of continuous warmth to soothe severe pelvic cramps.\",\n"
                    + "      \"pros\": \"Portable, long-lasting relief, non-medicated.\",\n"
                    + "      \"cons\": \"Single use only.\",\n"
                    + "      \"rating\": \"4.8/5\",\n"
                    + "      \"buyUrl\": \"[https://www.amazon.in/s?k=period+pain+relief+patches](https://www.amazon.in/s?k=period+pain+relief+patches)\",\n"
                    + "      \"image\": \"[https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600](https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600)\"\n"
                    + "    }\n"
                    + "  ]\n"
                    + "}\n"
                    + "Ensure you return AT LEAST 5 distinct products (e.g. Sanitary Pads/Cups, Herbal Teas, Multivitamins, Pain Patches, Intimate Wash). Use clean Amazon search URLs for buyUrl.";

            return executeGeminiPayload(prompt);

        } catch (Exception e) {
            return "{\"error\": \"Error processing AI products: " + e.getMessage() + "\"}";
        }
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

            GeminiResponse response = webClient.post()
                    .uri(geminiApiUrl + "?key=" + geminiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonBody)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .timeout(Duration.ofSeconds(12))
                    .block();

            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                var candidate = response.getCandidates().get(0);
                if (candidate.getContent() != null && candidate.getContent().getParts() != null && !candidate.getContent().getParts().isEmpty()) {
                    String text = candidate.getContent().getParts().get(0).getText();
                    return text.replace("```json", "").replace("```", "").trim();
                }
            }
            return "{\"error\": \"No response from Gemini AI\"}";

        } catch (WebClientResponseException.TooManyRequests e) {
            return "{\"error\": \"API Rate limit exceeded (HTTP 429). Please wait 1 minute.\"}";
        } catch (Exception e) {
            return "{\"error\": \"Error calling AI service: " + e.getMessage() + "\"}";
        }
    }

    // ================= INNER DTO CLASSES =================

    public static class ProductSearchRequest {
        private List<String> symptoms;
        private String customQuery;

        public List<String> getSymptoms() { return symptoms; }
        public void setSymptoms(List<String> symptoms) { this.symptoms = symptoms; }

        public String getCustomQuery() { return customQuery; }
        public void setCustomQuery(String customQuery) { this.customQuery = customQuery; }
    }

    public static class GeminiResponse {
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