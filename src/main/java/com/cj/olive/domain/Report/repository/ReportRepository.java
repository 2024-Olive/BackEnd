package com.cj.olive.domain.Report.repository;

import com.cj.olive.domain.Report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUserIdOrderByRegTimeDesc(Long userId);

    List<Report> findByUserUsernameOrderByRegTimeDesc(String username);

    List<Report> findAllByOrderByRegTimeDesc();
}
