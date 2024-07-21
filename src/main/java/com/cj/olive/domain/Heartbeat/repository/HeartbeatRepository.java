package com.cj.olive.domain.Heartbeat.repository;

import com.cj.olive.domain.Heartbeat.entity.Heartbeat;
import com.cj.olive.domain.User.entity.User;
import com.cj.olive.presentation.dto.res.Heartbeat.HeartbeatStaticResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface HeartbeatRepository extends JpaRepository<Heartbeat, Long> {

    @Query("SELECT DATE(h.regTime) as date, " +
            "MIN(h.minBpm) as minBpm, MAX(h.maxBpm) as maxBpm, u.username as username " +
            "FROM Heartbeat h " +
            "JOIN h.user u " +
            "GROUP BY DATE(h.regTime), u.id, u.username " +
            "ORDER BY DATE(h.regTime) DESC")
    List<Object[]> findHeartbeatStatsGroupedByUserAndDate();

    @Query("SELECT new com.cj.olive.presentation.dto.res.Heartbeat.HeartbeatStaticResDto(" +
            "HOUR(h.regTime), " +
            "AVG(h.avgBpm), " +
            "MIN(h.minBpm), " +
            "MAX(h.maxBpm)) " +
            "FROM Heartbeat h " +
            "WHERE h.user = :user AND h.regTime BETWEEN :start AND :end " +
            "GROUP BY HOUR(h.regTime)")
    List<HeartbeatStaticResDto> findHourlyAvgBpmByUserAndDate(
            @Param("user") User user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT new com.cj.olive.presentation.dto.res.Heartbeat.HeartbeatStaticResDto(" +
            "DATE(h.regTime), " +
            "AVG(h.avgBpm), " +
            "MIN(h.minBpm), " +
            "MAX(h.maxBpm)) " +
            "FROM Heartbeat h " +
            "WHERE h.user = :user AND h.regTime BETWEEN :start AND :end " +
            "GROUP BY DATE(h.regTime)")
    List<HeartbeatStaticResDto> findDailyAvgBpmInWeekByUserAndDate(
            @Param("user") User user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT new com.cj.olive.presentation.dto.res.Heartbeat.HeartbeatStaticResDto(" +
            "EXTRACT(WEEK FROM h.regTime), " +
            "AVG(h.avgBpm), " +
            "MIN(h.minBpm), " +
            "MAX(h.maxBpm)) " +
            "FROM Heartbeat h " +
            "WHERE h.user = :user AND h.regTime BETWEEN :start AND :end " +
            "GROUP BY EXTRACT(WEEK FROM h.regTime)")
    List<HeartbeatStaticResDto> findWeeklyAvgBpmInMonthByUserAndDate(
            @Param("user") User user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}