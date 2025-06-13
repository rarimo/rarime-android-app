package com.rarilabs.rarime.util

import android.content.Context
import com.rarilabs.rarime.R
import com.rarilabs.rarime.api.voting.models.Poll
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
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
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME -> OffsetDateTime.parse(
                        dateStr,
                        formatter
                    ).toLocalDateTime()

                    DateTimeFormatter.ISO_ZONED_DATE_TIME -> ZonedDateTime.parse(dateStr, formatter)
                        .toLocalDateTime()

                    DateTimeFormatter.ISO_LOCAL_DATE_TIME -> LocalDateTime.parse(dateStr, formatter)
                    DateTimeFormatter.ISO_INSTANT -> LocalDateTime.ofInstant(
                        OffsetDateTime.parse(
                            dateStr,
                            formatter
                        ).toInstant(), ZoneOffset.UTC
                    )

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

    fun convertToMrzDate(date: String?): String {
        val formatter = SimpleDateFormat(DateFormatType.DMY.pattern, Locale.US)
        val parsedDate = stringToDate(date, formatter) ?: return ""
        return formatDate(parsedDate, DateFormatType.MRZ)
    }

    fun formatDate(date: Date, formatType: DateFormatType = DateFormatType.DEFAULT): String {
        return SimpleDateFormat(formatType.pattern, Locale.US).format(date)
    }


    fun yearsBetween(from: Date): Int {
        val now = Calendar.getInstance()
        val fromCal = Calendar.getInstance().apply {
            time = from
        }

        var years = now.get(Calendar.YEAR) - fromCal.get(Calendar.YEAR)

        // If today's date is before the birth date (month and day), subtract one year
        if (now.get(Calendar.DAY_OF_YEAR) < fromCal.get(Calendar.DAY_OF_YEAR)) {
            years--
        }

        return years
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
            val format = SimpleDateFormat(pattern, Locale.US)
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.format(date)
        } else {
            ""
        }
    }

    fun duration(from: LocalDateTime, to: LocalDateTime = LocalDateTime.now()): Duration {
        return Duration.between(from, to)
    }

    fun getDurationString(duration: Duration, context: Context): String {
        return when {
            duration.toDays() > 0 -> context.getString(
                R.string.default_duration_days,
                duration.toDays().toString()
            )

            duration.toHours() > 0 -> context.getString(
                R.string.default_duration_hours,
                duration.toHours().toString()
            )

            duration.toMinutes() > 0 -> context.getString(
                R.string.default_duration_minutes,
                duration.toMinutes().toString()
            )

            duration.seconds > 0 -> context.getString(
                R.string.default_duration_seconds,
                duration.seconds.toString()
            )

            else -> ""
        }
    }

    fun getDateMessage(poll: Poll, context: Context): String {
        return when {
            poll.isEnded -> context.getString(R.string.poll_voting_ended)
            poll.isStarted -> {
                // Voting ends in some relative time period.
                val remaining = poll.voteEndDate?.let { getRelativeTimeMessage(it) } ?: "N/A"
                context.getString(R.string.poll_voting_end_timer, remaining)
            }

            else -> {
                // Voting starts in some relative time period.
                val remaining = poll.voteStartDate?.let { getRelativeTimeMessage(it) } ?: "N/A"
                context.getString(R.string.poll_voting_start_timer, remaining)
            }
        }
    }

    fun getRelativeTimeMessage(timestampSeconds: Long): String {
        val currentTimeMillis = System.currentTimeMillis()
        val targetMillis = timestampSeconds * 1000
        val diffMillis = targetMillis - currentTimeMillis

        // If the target time is in the past, return "0s"
        if (diffMillis <= 0L) return "0s"

        val seconds = diffMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        // For longer durations, we show months (approximate: 30 days per month)
        return when {
            days >= 45 -> {
                val months = days / 30 // approximation
                "$months month" + if (months > 1) "s" else ""
            }

            days >= 1 -> "$days day" + if (days > 1) "s" else ""
            hours >= 1 -> "$hours hour" + if (hours > 1) "s" else ""
            minutes >= 1 -> "$minutes minute" + if (minutes > 1) "s" else ""
            else -> "$seconds second" + if (seconds > 1) "s" else ""
        }
    }

    fun LocalDateTime.toDate(): Date {
        return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
    }
}
