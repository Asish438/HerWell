package com.HerWell.example.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "symptom_record")
public class SymptomRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long internalId;

    @ElementCollection(fetch = FetchType.EAGER) // 👈 ADD (fetch = FetchType.EAGER) HERE!
    private List<String> symptoms;

    private String geminiResponse;

    private LocalDateTime createdAt = LocalDateTime.now();

    public SymptomRecord() {}

    // Getters and Setters...
    public Long getInternalId() { return internalId; }
    public void setInternalId(Long internalId) { this.internalId = internalId; }

    public List<String> getSymptoms() { return symptoms; }
    public void setSymptoms(List<String> symptoms) { this.symptoms = symptoms; }

    public String getGeminiResponse() { return geminiResponse; }
    public void setGeminiResponse(String geminiResponse) { this.geminiResponse = geminiResponse; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}