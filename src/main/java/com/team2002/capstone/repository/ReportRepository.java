package com.team2002.capstone.repository;

import com.team2002.capstone.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByReporterIdOrderByCreatedAtDesc(Long reporterId);
    List<Report> findAllByOrderByStatusAscCreatedAtDesc();
}
