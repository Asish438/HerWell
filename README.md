# 🌸 HerWell – AI-Powered Personal Women's Health & Wellness Platform

HerWell is a comprehensive, full-stack digital health application designed to empower women by offering personalized health tracking, AI-driven wellness insights, menstrual cycle tracking, and tailored diet & fitness recommendations.

---

## 🚀 Live Demo & Services

* **Frontend App:** [Live Static Web App](https://bucolic-cranachan-fd359e.netlify.app)
* **Backend API Service:** `https://herwell-backend.onrender.com`
* **Database:** Managed PostgreSQL Instance on Render

---

## ✨ Features

* **AI-Driven Health Insights:** Personalized wellness suggestions based on user symptoms and physiological data.
* **Menstrual Cycle & Symptom Tracker:** Logs cycle dates, moods, physical symptoms, and provides predictive timelines.
* **Tailored Diet & Fitness Plans:** Automatically generates customized nutrition guides and BMI-based exercise schedules.
* **User Authentication & Profile Management:** Secure registration, login, and personal health metrics storage.
* **Community Blogs & Products Directory:** Curated resources, blogs, and health products focused on women's hygiene and wellness.

---

## 🛠️ Tech Stack & Architecture

### **Frontend**
* **Languages:** HTML5, CSS3, Modern JavaScript (ES6+)
* **Deployment:** Netlify Drop / Render Static Sites

### **Backend**
* **Framework:** Java 21, Spring Boot
* **Security & APIs:** RESTful APIs, CORS Management, JWT / Session Authentication
* **Containerization:** Docker
* **Deployment:** Render (Dockerized Web Service)

### **Database**
* **Database Management System:** PostgreSQL 16
* **ORM:** Spring Data JPA / Hibernate

---

## 📁 Repository Structure

```text
HerWell/
├── HerWell/                     # Frontend Source Files
│   ├── after login/             # Authenticated user pages (dashboard, bmi, diet, etc.)
│   ├── css/                     # Stylesheets
│   ├── js/                      # Frontend JavaScript logic
│   ├── index.html               # Main Landing Page
│   ├── login.html               # User Login
│   ├── signup.html              # User Registration
│   └── symptom.html             # Symptom logging interface
├── src/                         # Spring Boot Backend Source Code
│   ├── main/java/               # Java Controllers, Services, Models, Repositories
│   └── main/resources/          # application.properties & Static Assets
├── Dockerfile                   # Multi-stage Docker build configuration
├── pom.xml                      # Maven Dependencies
└── README.md                    # Project Documentation
