package com.HerWell.example.Service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BudgetGuardService {

    // Approx 10,000 requests ≈ ₹140-₹150 limit for Gemini Flash
    private static final int MAX_MONTHLY_REQUESTS = 10000;

    private final AtomicInteger monthlyCounter = new AtomicInteger(0);
    private int currentMonth = LocalDate.now().getMonthValue();

    public synchronized boolean isBudgetSafe() {
        int nowMonth = LocalDate.now().getMonthValue();

        // Auto-reset counter on the 1st day of every month
        if (nowMonth != currentMonth) {
            currentMonth = nowMonth;
            monthlyCounter.set(0);
        }

        return monthlyCounter.get() < MAX_MONTHLY_REQUESTS;
    }

    public void incrementUsage() {
        monthlyCounter.incrementAndGet();
    }

    public int getCurrentUsageCount() {
        return monthlyCounter.get();
    }
}