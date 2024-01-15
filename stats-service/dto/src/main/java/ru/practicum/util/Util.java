package ru.practicum.util;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Util {

	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static final LocalDateTime START_HISTORY = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
}
