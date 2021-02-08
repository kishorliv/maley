package com.example.maley.utils;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class DateAndTime {

    public static String currentTime(){
        ZoneId zoneId = ZoneId.of("Australia/Melbourne");
        LocalDateTime localTime = LocalDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy - HH:mm");
        return localTime.format(formatter);
    }

    public static String currentDate(){
        ZoneId zoneId = ZoneId.of("Australia/Melbourne");
        LocalDateTime localTime = LocalDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return localTime.format(formatter);
    }

    // returns tomorrow's date based on today's date
    public static String tomorrowDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate =  LocalDate.parse(date, formatter);
        return localDate.plusDays(1).format(formatter);
    }

    // returns yesterday's date based on today's date
    public static String yesterdayDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate =  LocalDate.parse(date, formatter);
        return localDate.minusDays(1).format(formatter);
    }

}
