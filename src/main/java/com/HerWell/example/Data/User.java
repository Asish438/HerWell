package com.HerWell.example.Data;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    private Integer age;
    private Double height; // in cm
    private Double weight; // in kg
    private String bloodGroup;

    private Boolean sugarPatient;
    private Boolean bloodPressurePatient;
    private Boolean gastricIssue;

    public User() {}

    public User(String email, String password, String fullName, Integer age, Double height, Double weight,
                String bloodGroup, Boolean sugarPatient, Boolean bloodPressurePatient, Boolean gastricIssue) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.bloodGroup = bloodGroup;
        this.sugarPatient = sugarPatient;
        this.bloodPressurePatient = bloodPressurePatient;
        this.gastricIssue = gastricIssue;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public Boolean getSugarPatient() { return sugarPatient; }
    public void setSugarPatient(Boolean sugarPatient) { this.sugarPatient = sugarPatient; }

    public Boolean getBloodPressurePatient() { return bloodPressurePatient; }
    public void setBloodPressurePatient(Boolean bloodPressurePatient) { this.bloodPressurePatient = bloodPressurePatient; }

    public Boolean getGastricIssue() { return gastricIssue; }
    public void setGastricIssue(Boolean gastricIssue) { this.gastricIssue = gastricIssue; }
}
