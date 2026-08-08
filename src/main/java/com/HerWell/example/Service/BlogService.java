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
public class BlogService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public BlogService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    public String generateDynamicBlogs(List<String> symptoms, String customQuery) {
        try {
            String topicContext = (customQuery != null && !customQuery.trim().isEmpty())
                    ? "Topic: " + customQuery
                    : "Logged Symptoms: " + (symptoms != null ? String.join(", ", symptoms) : "General Wellness");

            String prompt = "You are HerWell AI, a women's health digital editor.\n"
                    + "Based on the input: [" + topicContext + "], generate AT LEAST 5 dynamic article & video recommendations.\n\n"
                    + "STRICT REQUIREMENT: Respond ONLY with a valid JSON object matching this exact structure, with NO extra markdown wrap or markdown code blocks (no ```json):\n"
                    + "{\n"
                    + "  \"items\": [\n"
                    + "    {\n"
                    + "      \"type\": \"article\",\n"
                    + "      \"category\": \"Menstrual Health\",\n"
                    + "      \"title\": \"Catchy Title Here\",\n"
                    + "      \"summary\": \"Brief medical explanation (2-3 sentences).\",\n"
                    + "      \"author\": \"Medical Reviewer / Expert Name\",\n"
                    + "      \"url\": \"[https://www.healthline.com/](https://www.healthline.com/)...\",\n"
                    + "      \"image\": \"[https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600](https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600)\"\n"
                    + "    },\n"
                    + "    {\n"
                    + "      \"type\": \"video\",\n"
                    + "      \"category\": \"Hormonal Health\",\n"
                    + "      \"title\": \"Video Title Here\",\n"
                    + "      \"summary\": \"Brief description of video lecture.\",\n"
                    + "      \"author\": \"Dr. Expert • 7 min video\",\n"
                    + "      \"url\": \"[https://www.youtube.com/watch?v=](https://www.youtube.com/watch?v=)...\",\n"
                    + "      \"image\": \"[https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=600](https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=600)\"\n"
                    + "    }\n"
                    + "  ]\n"
                    + "}\n"
                    + "Ensure you return AT LEAST 5 items (mix of 'article' and 'video'). Use valid real URLs or trustworthy search URLs.";

            return executeGeminiPayload(prompt);

        } catch (Exception e) {
            return "{\"error\": \"Error processing AI blogs: " + e.getMessage() + "\"}";
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

    public static class BlogSearchRequest {
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