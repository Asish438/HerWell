package com.HerWell.example.Controller;

import com.HerWell.example.Service.DailyHealthService;
import com.HerWell.example.Service.DailyHealthService.SymptomsDietResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DailyHealthController {

    @Autowired
    private DailyHealthService dailyHealthService;

    // Endpoint to get diet based on user symptoms
    @PostMapping("/symptoms-diet")
    public SymptomsDietResponse getSymptomsDiet(@RequestBody UserSymptomsInput input) {
        return dailyHealthService.getSymptomsDiet(
                input.getWhiteDischarge(),
                input.getHemoglobinLevel(),
                input.getInfections(),
                input.getSkinCondition(),
                input.getSleepProblems(),
                input.getHairCondition(),
                input.getMood()
        );
    }

    // DTO to receive user input from frontend
    public static class UserSymptomsInput {
        private String whiteDischarge;
        private String hemoglobinLevel;
        private String infections;
        private String skinCondition;
        private String sleepProblems;
        private String hairCondition;
        private String mood;

        // Getters and setters
        public String getWhiteDischarge() { return whiteDischarge; }
        public void setWhiteDischarge(String whiteDischarge) { this.whiteDischarge = whiteDischarge; }

        public String getHemoglobinLevel() { return hemoglobinLevel; }
        public void setHemoglobinLevel(String hemoglobinLevel) { this.hemoglobinLevel = hemoglobinLevel; }

        public String getInfections() { return infections; }
        public void setInfections(String infections) { this.infections = infections; }

        public String getSkinCondition() { return skinCondition; }
        public void setSkinCondition(String skinCondition) { this.skinCondition = skinCondition; }

        public String getSleepProblems() { return sleepProblems; }
        public void setSleepProblems(String sleepProblems) { this.sleepProblems = sleepProblems; }

        public String getHairCondition() { return hairCondition; }
        public void setHairCondition(String hairCondition) { this.hairCondition = hairCondition; }

        public String getMood() { return mood; }
        public void setMood(String mood) { this.mood = mood; }
    }
}
