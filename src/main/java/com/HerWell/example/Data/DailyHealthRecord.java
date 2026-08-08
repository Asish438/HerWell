package com.HerWell.example.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DailyHealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String whiteDischarge;  // Yes/No
    private String hemoglobinLevel; // High/Medium/Low
    private String infections;      // Yes/No
    private String skinCondition;   // Good/Oily/Dry/Acne
    private String sleepProblems;   // Yes/No
    private String hairCondition;   // Good/Oily/Dry/Bad
    private String mood;            // Happy/Neutral/Sensitive/Irritable/Sad

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters & setters
    // ...
}
