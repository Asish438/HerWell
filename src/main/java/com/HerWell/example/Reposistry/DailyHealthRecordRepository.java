package com.HerWell.example.Reposistry;

import com.HerWell.example.Data.DailyHealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyHealthRecordRepository extends JpaRepository<DailyHealthRecord, Long> {
}
