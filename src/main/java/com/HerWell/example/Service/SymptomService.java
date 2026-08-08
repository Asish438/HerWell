package com.HerWell.example.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.HerWell.example.Data.SymptomRecord;
import com.HerWell.example.Reposistry.SymptomRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
public class SymptomService {

    @Autowired
    private SymptomRecordRepository repository;

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

    public SymptomService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    // 1️⃣ Save symptoms in DB
    public SymptomRecord processSymptoms(List<String> symptoms) {
        SymptomRecord record = new SymptomRecord();
        record.setSymptoms(symptoms);
        return repository.save(record);
    }

    // 2️⃣ Get all raw records
    public List<SymptomRecord> getAllRecords() {
        return repository.findAll();
    }

    // 3️⃣ Fast Execution: Process ONLY the latest submitted record (Fixes 429 & Loop Issue)
    @Transactional(readOnly = true)
    public List<FormattedRecord> getElaboratedRecords() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Fetch ONLY the single newest record from DB
        SymptomRecord latestRecord = repository.findTopByOrderByInternalIdDesc()
                .orElse(null);

        if (latestRecord == null) {
            return List.of();
        }

        String elaborated = callGeminiElaborate(latestRecord.getSymptoms());

        FormattedRecord formatted = new FormattedRecord(
                latestRecord.getSymptoms(),
                elaborated,
                latestRecord.getCreatedAt() != null ? latestRecord.getCreatedAt().format(formatter) : "N/A"
        );

        return List.of(formatted);
    }

    private String callGeminiElaborate(List<String> symptoms) {
        if (symptoms == null || symptoms.isEmpty()) {
            return "No symptoms recorded for evaluation.";
        }

        String reportedSymptomsText = String.join(", ", symptoms);

        String prompt = "You are HerWell AI, Senior Gynecologist. Evaluate symptoms: [" + reportedSymptomsText + "].\n"
                + "Provide concise medical evaluation in Markdown using EXACT headers:\n\n"
                + "### 📋 EXECUTIVE CLINICAL SUMMARY\n"
                + "- Empathetic overview.\n\n"
                + "### 🔍 MEDICAL ANALYSIS & POSSIBLE CAUSES\n"
                + "- Underlying causes.\n\n"
                + "### 🥗 RECOMMENDED DIET & FOODS TO EAT\n"
                + "- Useful food and nutrients.\n\n"
                + "### 🚫 FOODS & HABITS TO AVOID & DIET PLAN\n"
                + "- Things to avoid.\n\n"
                + "### 🧘 DAILY ROUTINE & LIFESTYLE PRECAUTIONS\n"
                + "- Sleep, routine, precautions.\n\n"
                + "### 🚨 WHEN TO CONSULT A DOCTOR IMMEDIATELY\n"
                + "- Red-flag warning symptoms.";

        return executeGeminiPayload(prompt);
    }

    // 4️⃣ Multi-turn AI Chat Assistant with Guardrails
    public ChatResponse processChatConversation(ChatRequest chatRequest) {
        try {
            String systemInstruction = "You are HerWell AI, a professional women's health consultant.\n"
                    + "STRICT GUARDRAIL: Only answer female health, menstrual, and medical queries.\n"
                    + "If non-medical, decline with: \"⛔ REQUEST DECLINED: HerWell AI only answers health questions.\"\n"
                    + "For health queries, use Markdown headers (###), bullet points, and clear advice.";

            List<Map<String, Object>> contents = new ArrayList<>();

            contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", systemInstruction))));
            contents.add(Map.of("role", "model", "parts", List.of(Map.of("text", "Understood. I will provide structured medical advice."))));

            // Append last 4 messages from history to keep token payload light
            if (chatRequest.getHistory() != null) {
                List<ChatMessage> history = chatRequest.getHistory();
                int start = Math.max(0, history.size() - 4);
                for (int i = start; i < history.size(); i++) {
                    ChatMessage msg = history.get(i);
                    String role = "user".equalsIgnoreCase(msg.getSender()) ? "user" : "model";
                    contents.add(Map.of("role", role, "parts", List.of(Map.of("text", msg.getText()))));
                }
            }

            contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", chatRequest.getNewQuestion()))));

            String payloadJson = objectMapper.writeValueAsString(Map.of("contents", contents));
            String reply = executeGeminiJsonBody(payloadJson);

            return new ChatResponse(reply);

        } catch (Exception e) {
            return new ChatResponse("Error processing AI chat: " + e.getMessage());
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
            return executeGeminiJsonBody(jsonBody);
        } catch (Exception e) {
            return "Error serializing request: " + e.getMessage();
        }
    }

    private String executeGeminiJsonBody(String jsonBody) {
        if (!budgetGuardService.isBudgetSafe()) {
            return "⚠️ **Monthly Budget Limit Exceeded:** HerWell AI ka monthly API threshold (approx ₹150) touch ho gaya hai.";
        }

        try {
            return makeGeminiCall(jsonBody, geminiApiKeyPrimary);
        } catch (WebClientResponseException.TooManyRequests e) {
            System.out.println("⚠️ HTTP 429 Rate Limit encountered on Primary Key. Retrying with Secondary Key...");

            if (geminiApiKeySecondary != null && !geminiApiKeySecondary.isBlank()) {
                try {
                    return makeGeminiCall(jsonBody, geminiApiKeySecondary);
                } catch (Exception ex) {
                    return "⚠️ **Rate Limit Exceeded (HTTP 429):** Backup key limit is also exhausted. Please wait 1 minute.";
                }
            }
            return "⚠️ **Rate Limit Exceeded (HTTP 429):** API quota limit exceed ho gayi hai. Kripya 1 minute baad try karein.";
        } catch (Exception e) {
            if (e.getCause() instanceof TimeoutException || e instanceof TimeoutException) {
                return "⚠️ **Timeout Error:** Response lene me time lag raha hai. Kripya punah prayas karein.";
            }
            return "⚠️ Error connecting to Gemini API: " + e.getMessage();
        }
    }

    private String makeGeminiCall(String jsonBody, String apiKey) {
        GeminiResponse response = webClient.post()
                .uri(geminiApiUrl + "?key=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonBody)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .timeout(Duration.ofSeconds(20))
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

    // ================= DTO CLASSES =================

    public static class ChatRequest {
        private String newQuestion;
        private List<ChatMessage> history = new ArrayList<>();

        public String getNewQuestion() { return newQuestion; }
        public void setNewQuestion(String newQuestion) { this.newQuestion = newQuestion; }
        public List<ChatMessage> getHistory() { return history; }
        public void setHistory(List<ChatMessage> history) { this.history = history; }
    }

    public static class ChatMessage {
        private String sender;
        private String text;

        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    public static class ChatResponse {
        private String answer;
        public ChatResponse(String answer) { this.answer = answer; }
        public String getAnswer() { return answer; }
    }

    public static class FormattedRecord {
        private List<String> symptoms;
        private String aiAdvice;
        private String recordedAt;

        public FormattedRecord(List<String> symptoms, String aiAdvice, String recordedAt) {
            this.symptoms = symptoms;
            this.aiAdvice = aiAdvice;
            this.recordedAt = recordedAt;
        }

        public List<String> getSymptoms() { return symptoms; }
        public String getAiAdvice() { return aiAdvice; }
        public String getRecordedAt() { return recordedAt; }
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