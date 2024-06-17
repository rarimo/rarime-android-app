package com.distributedLab.rarime.util

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

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

    fun stringToLocalDateTime(dateStr: String?, dateFormat: DateTimeFormatter): LocalDateTime? {
        return try {
            LocalDateTime.parse(dateStr, dateFormat)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        val targetDate = stringToLocalDateTime(dateStr, dateFormat)

        if (targetDate != null) {
            return getTimeLeft(targetDate)
        } else {
            throw Exception("Invalid date format")
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
}
