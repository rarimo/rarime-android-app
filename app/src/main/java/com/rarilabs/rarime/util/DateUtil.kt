package com.rarilabs.rarime.util

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class DateFormatType(val pattern: String) {
    DEFAULT("dd MMM, YYYY"),
    DMY("dd.MM.yyyy"),
    MRZ("yyMMdd")
}

object DateUtil {
    private fun stringToDate(dateStr: String?, dateFormat: DateFormat): Date? {
        var date: Date? = null
        try {
            date = dateFormat.parse(dateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    fun parseDateString(dateStr: String): LocalDateTime? {
        val formatters = listOf(
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,  // e.g., "2024-08-01T00:00:00Z"
            DateTimeFormatter.ISO_ZONED_DATE_TIME,   // e.g., "2024-08-01T00:00:00+01:00[Europe/London]"
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,   // e.g., "2024-08-01T00:00:00"
            DateTimeFormatter.ISO_INSTANT            // e.g., "2024-08-01T00:00:00.000Z"
        )

        for (formatter in formatters) {
            try {
                return when (formatter) {
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME -> OffsetDateTime.parse(dateStr, formatter).toLocalDateTime()
                    DateTimeFormatter.ISO_ZONED_DATE_TIME -> ZonedDateTime.parse(dateStr, formatter).toLocalDateTime()
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME -> LocalDateTime.parse(dateStr, formatter)
                    DateTimeFormatter.ISO_INSTANT -> LocalDateTime.ofInstant(OffsetDateTime.parse(dateStr, formatter).toInstant(), ZoneOffset.UTC)
                    else -> null
                }
            } catch (e: DateTimeParseException) {
                // Continue to the next formatter
            }
        }
        return null
    }

    fun getTimeLeft(targetDate: LocalDateTime): String {
        val now = LocalDateTime.now()
        val duration = Duration.between(now, targetDate)

        return when {
            duration.toDays() > 0 -> "${duration.toDays()} days left"
            duration.toHours() > 0 -> "${duration.toHours()} hours left"
            duration.toMinutes() > 0 -> "${duration.toMinutes()} minutes left"
            duration.seconds > 0 -> "${duration.seconds} seconds left"
            else -> "Time's up!"
        }
    }

    fun stringToTimeLeft(dateStr: String): String {
        try {
            val targetDate = parseDateString(dateStr)

            if (targetDate != null) {
                return getTimeLeft(targetDate)
            } else {
                throw Exception("Invalid date format")
            }
        } catch (e: Exception) {
            return ""
        }
    }

    fun convertFromMrzDate(mrzDate: String?): String {
        val date = stringToDate(mrzDate, SimpleDateFormat(DateFormatType.MRZ.pattern, Locale.US))
            ?: return ""
        return formatDate(date, DateFormatType.DMY)
    }

    fun formatDate(date: Date, formatType: DateFormatType = DateFormatType.DEFAULT): String {
        return SimpleDateFormat(formatType.pattern, Locale.US).format(date)
    }

    fun formatDateString(
        dateStr: String?,
        inputFormat: DateFormatType = DateFormatType.DMY,
        outputFormat: DateFormatType = DateFormatType.DMY,
    ): String {
        val date = stringToDate(dateStr, SimpleDateFormat(inputFormat.pattern, Locale.US))
            ?: return ""
        return formatDate(date, outputFormat)
    }

    fun convertToDate(value: Long?, pattern: String = "d/M/yyyy"): String {
        return if (value != null) {
            val date = Date(value)
            val format = SimpleDateFormat(pattern, Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.format(date)
        } else {
            ""
        }
    }
}
