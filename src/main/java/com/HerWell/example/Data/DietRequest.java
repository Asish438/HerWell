package com.HerWell.example.Data;

import java.util.ArrayList;
import java.util.List;

public class DietRequest {
    private String bleeding;
    private String energy;
    private String exercise;
    private String goal;
    private String dietType;
    private String mealsPerDay;
    private List<String> allergies = new ArrayList<>();
    private List<String> medicalConditions = new ArrayList<>();

    // Getters and Setters
    public String getBleeding() { return bleeding; }
    public void setBleeding(String bleeding) { this.bleeding = bleeding; }

    public String getEnergy() { return energy; }
    public void setEnergy(String energy) { this.energy = energy; }

    public String getExercise() { return exercise; }
    public void setExercise(String exercise) { this.exercise = exercise; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public String getDietType() { return dietType; }
    public void setDietType(String dietType) { this.dietType = dietType; }

    public String getMealsPerDay() { return mealsPerDay; }
    public void setMealsPerDay(String mealsPerDay) { this.mealsPerDay = mealsPerDay; }

    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }

    public List<String> getMedicalConditions() { return medicalConditions; }
    public void setMedicalConditions(List<String> medicalConditions) { this.medicalConditions = medicalConditions; }
}