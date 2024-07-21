package com.cj.olive.global.util;

import com.cj.olive.domain.Heartbeat.error.HeartbeatErrorCode;
import com.cj.olive.global.error.exception.AppException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm");

    // 날짜 매핑
    public static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }

    // 시간 매핑
    public static String formatHour(LocalDateTime dateTime) {
        return dateTime.format(TIME_FORMATTER);
    }

    // 날짜 문자열을 LocalDate로 변환
    public static LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    // 유효한 날인지 검사
    public static void validateDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new AppException(HeartbeatErrorCode.INVALID_DATE);
        }
    }

    // 유효한 기간인지 검사
    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
            throw new AppException(HeartbeatErrorCode.INVALID_DATE);
        }
    }

    // date 를 localdate로 변환
    public static LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
