package ru.job4j.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Класс реализует преобразование даты читаемоей с сайта sql.ru в
 * LocalDateTime формат.
 * Внимание! корректно работает только с датами, год которых более 2000
 * @author Alex Ter (ShaDar-Ru)
 * @version 1.0
 */

public class SqlRuDateTimeParser implements DateTimeParser {

    private static final Map<String, String> MONTH = Map.ofEntries(
            Map.entry("янв", "01"),
            Map.entry("фев", "02"),
            Map.entry("мар", "03"),
            Map.entry("апр", "04"),
            Map.entry("май", "05"),
            Map.entry("июн", "06"),
            Map.entry("июл", "07"),
            Map.entry("авг", "08"),
            Map.entry("сен", "09"),
            Map.entry("окт", "10"),
            Map.entry("ноя", "11"),
            Map.entry("дек", "12")
    );

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d M yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public LocalDateTime parse(String parse) {

        String[] dateArray = parse.split(", ");
        String dateFromString = dateArray[0];
        String timeFromString = dateArray[1].trim();
        String[] date = dateFromString.split(" ");

        String tempDay;

        LocalDate today = LocalDate.now();

        if (parse.toLowerCase().contains("сегодня")) {
            tempDay = today.format(dateFormatter);
        } else if (parse.toLowerCase().contains("вчера")) {
            tempDay = today.minusDays(1).format(dateFormatter);
        } else {
            String year = String.valueOf(Integer.parseInt(date[2]) + 2000);
            tempDay = String.format("%s %s %s", date[0], MONTH.get(date[1]), year);
        }

        LocalDate day = LocalDate.parse(tempDay, dateFormatter);
        LocalTime time = LocalTime.parse(timeFromString, timeFormatter);
        
        return LocalDateTime.of(day, time);
    }
}
