package com.richjun.campride.report.domain.repository;

import com.richjun.campride.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
