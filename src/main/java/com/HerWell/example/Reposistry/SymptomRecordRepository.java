package com.HerWell.example.Reposistry;

import com.HerWell.example.Data.SymptomRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SymptomRecordRepository extends JpaRepository<SymptomRecord, Long> {

    // Fetch only the latest single record ordered by internalId descending
    Optional<SymptomRecord> findTopByOrderByInternalIdDesc();
}