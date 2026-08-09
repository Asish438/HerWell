<div align="center">

# 🌸 HerWell
### *Empowering Women's Health Through Smart Technology & AI Insights*

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Render](https://img.shields.io/badge/Render-Deployed-46E3B7?style=for-the-badge&logo=render&logoColor=black)

<p align="center">
  <a href="#-live-deployment"><b>Live Demo</b></a> •
  <a href="#-key-features"><b>Features</b></a> •
  <a href="#-tech-stack"><b>Tech Stack</b></a> •
  <a href="#-architecture--workflow"><b>Architecture</b></a> •
  <a href="#-getting-started"><b>Getting Started</b></a>
</p>

---

</div>

## 📌 Overview

**HerWell** is a modern, end-to-end digital health platform designed to help women track, understand, and optimize their daily health routines. From intelligent cycle forecasting and symptom logging to personalized nutrition plans, HerWell bridges the gap between raw health metrics and actionable daily wellness insights.

---

## 🌐 Live Deployment

| Service | Environment | Deployment Status | Link |
| :--- | :--- | :--- | :--- |
| **Frontend Web App** | Netlify Drop | ![Netlify](https://img.shields.io/badge/Netlify-Active-00C7B7?style=flat-square&logo=netlify) | [Launch App 🚀](https://bucolic-cranachan-fd359e.netlify.app) |
| **Backend REST API** | Render (Docker) | ![Render](https://img.shields.io/badge/Render-Live-46E3B7?style=flat-square&logo=render) | `https://herwell-backend.onrender.com` |
| **Database** | Render PostgreSQL | ![PostgreSQL](https://img.shields.io/badge/Postgres-Connected-4169E1?style=flat-square&logo=postgresql) | Managed Instance |

---

## ✨ Key Features

* **🩺 Symptom & Mood Intelligence:** Seamlessly record daily moods, physical symptoms, and energy levels to track long-term health trends.
* **📅 Cycle & Ovulation Forecasting:** Predictive logic to estimate cycle phases and fertility windows accurately.
* **🥗 Personalized Nutrition & Diet Plans:** Tailored dietary guidelines and meal routines optimized for specific wellness goals and BMI indices.
* **🔐 Secure Authentication:** JWT-backed user profiles guaranteeing privacy for sensitive health history.
* **🛍️ Hygiene & Product Directory:** Curated hub for essential personal hygiene products, wellness blogs, and health guides.

---

## 🛠️ Tech Stack

<details>
<summary><b>Frontend</b></summary>

* **Core:** HTML5, CSS3, Modern JavaScript (ES6+)
* **Styling:** Custom CSS Flexbox/Grid, Responsive Design
* **Hosting:** Netlify
</details>

<details>
<summary><b>Backend & API</b></summary>

* **Framework:** Spring Boot (Java 21)
* **Security & Auth:** Spring Security, JWT / Cross-Origin Request Management (CORS)
* **Build Tool:** Maven
* **Containerization:** Docker
* **Hosting:** Render
</details>

<details>
<summary><b>Database & Persistence</b></summary>

* **Database Engine:** PostgreSQL 16
* **ORM Layer:** Spring Data JPA / Hibernate
</details>

---

## 📐 Architecture & Directory Structure

```text
HerWell/
├── HerWell/                    # Static Web Client (Frontend)
│   ├── after login/            # Protected Dashboard Pages (BMI, Diet, Analytics)
│   ├── css/                    # Custom Stylesheets
│   ├── js/                     # Client API Handlers & Script Logic
│   ├── index.html              # Landing Page
│   ├── login.html              # User Authentication
│   └── symptom.html            # Symptom Tracker Portal
├── src/                        # Spring Boot Application Backend
│   ├── main/java/              # REST Controllers, Business Logic & JPA Entities
│   └── main/resources/         # App Configs & Database Bindings
├── Dockerfile                  # Container Deployment Configuration
└── pom.xml                     # Project Dependencies
