package com.HerWell.example.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_queries")
public class UserQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(length = 2000)
    private String queryText;

    private LocalDateTime createdAt = LocalDateTime.now();

    public UserQuery() {}

    public UserQuery(String email, String queryText) {
        this.email = email;
        this.queryText = queryText;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getQueryText() { return queryText; }
    public void setQueryText(String queryText) { this.queryText = queryText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
