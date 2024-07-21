package com.cj.olive.domain.Report.repository;

import com.cj.olive.domain.Report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUserId(Long userId);

    List<Report> findByUserUsername(String username);
}
