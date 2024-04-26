package com.distributedLab.rarime.util

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
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

    fun convertFromMrzDate(mrzDate: String?): String {
        val date = stringToDate(mrzDate, SimpleDateFormat(DateFormatType.MRZ.pattern, Locale.US))
            ?: return ""
        return formatDate(date, DateFormatType.DMY)
    }

    fun formatDate(date: Date, formatType: DateFormatType = DateFormatType.DEFAULT): String {
        return SimpleDateFormat(formatType.pattern, Locale.US).format(date)
    }
}
