package com.rochiee.classsync.taskengine

import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.abs

object DeadlineParser {
    private val monthLookup: Map<String, Int> by lazy {
        val monthNames = DateFormatSymbols(Locale.ENGLISH).months
        val shortMonthNames = DateFormatSymbols(Locale.ENGLISH).shortMonths
        buildMap {
            monthNames.forEachIndexed { index, value ->
                if (value.isNotBlank()) put(value.lowercase(Locale.ENGLISH), index)
            }
            shortMonthNames.forEachIndexed { index, value ->
                if (value.isNotBlank()) put(value.lowercase(Locale.ENGLISH).removeSuffix("."), index)
            }
        }
    }

    private val weekdayLookup = mapOf(
        "sunday" to Calendar.SUNDAY,
        "monday" to Calendar.MONDAY,
        "tuesday" to Calendar.TUESDAY,
        "wednesday" to Calendar.WEDNESDAY,
        "thursday" to Calendar.THURSDAY,
        "friday" to Calendar.FRIDAY,
        "saturday" to Calendar.SATURDAY
    )

    private val explicitMonthDayYear = Pattern.compile(
        "(?i)(?:due\\s*:?)?\\s*(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:t(?:ember)?)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)\\s+(\\d{1,2})(?:,\\s*(\\d{4}))?(?:\\s+(\\d{1,2}:\\d{2}\\s*(?:am|pm)|\\d{1,2}:\\d{2}))?"
    )
    private val explicitDayMonthYear = Pattern.compile(
        "(?i)(?:due\\s*:?)?\\s*(\\d{1,2})\\s+(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:t(?:ember)?)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)(?:,\\s*(\\d{4}))?(?:\\s+(\\d{1,2}:\\d{2}\\s*(?:am|pm)|\\d{1,2}:\\d{2}))?"
    )
    private val todayTomorrowPattern = Pattern.compile(
        "(?i)(?:due\\s*)?(today|tomorrow)(?:\\s+(\\d{1,2}:\\d{2}\\s*(?:am|pm)|\\d{1,2}:\\d{2}))?"
    )
    private val weekdayPattern = Pattern.compile(
        "(?i)(?:due\\s*)?(next\\s+)?(monday|tuesday|wednesday|thursday|friday|saturday|sunday)(?:\\s+(\\d{1,2}:\\d{2}\\s*(?:am|pm)|\\d{1,2}:\\d{2}))?"
    )
    private val timeOnlyPattern = Pattern.compile(
        "(?i)(?:due\\s*)?(\\d{1,2}:\\d{2}\\s*(?:am|pm)|\\d{1,2}:\\d{2})"
    )

    fun parse(text: String, nowMillis: Long = System.currentTimeMillis()): Long? {
        val cleaned = text.replace("\n", " ").trim()
        if (cleaned.isBlank()) return null

        parseTodayTomorrow(cleaned, nowMillis)?.let { return it }
        parseMonthDay(cleaned, nowMillis)?.let { return it }
        parseDayMonth(cleaned, nowMillis)?.let { return it }
        parseWeekday(cleaned, nowMillis)?.let { return it }
        parseTimeOnly(cleaned, nowMillis)?.let { return it }

        return null
    }

    private fun parseTodayTomorrow(text: String, nowMillis: Long): Long? {
        val matcher = todayTomorrowPattern.matcher(text)
        if (!matcher.find()) return null

        val base = calendarAt(nowMillis)
        when (matcher.group(1)?.lowercase(Locale.ENGLISH)) {
            "today" -> Unit
            "tomorrow" -> base.add(Calendar.DAY_OF_YEAR, 1)
            else -> return null
        }
        applyTime(base, matcher.group(2))
        return base.timeInMillis
    }

    private fun parseMonthDay(text: String, nowMillis: Long): Long? {
        val matcher = explicitMonthDayYear.matcher(text)
        if (!matcher.find()) return null
        val month = monthLookup[matcher.group(1)?.lowercase(Locale.ENGLISH)?.removeSuffix(".")] ?: return null
        val day = matcher.group(2)?.toIntOrNull() ?: return null
        val year = matcher.group(3)?.toIntOrNull()
        return buildExplicitDate(nowMillis, year, month, day, matcher.group(4))
    }

    private fun parseDayMonth(text: String, nowMillis: Long): Long? {
        val matcher = explicitDayMonthYear.matcher(text)
        if (!matcher.find()) return null
        val day = matcher.group(1)?.toIntOrNull() ?: return null
        val month = monthLookup[matcher.group(2)?.lowercase(Locale.ENGLISH)?.removeSuffix(".")] ?: return null
        val year = matcher.group(3)?.toIntOrNull()
        return buildExplicitDate(nowMillis, year, month, day, matcher.group(4))
    }

    private fun parseWeekday(text: String, nowMillis: Long): Long? {
        val matcher = weekdayPattern.matcher(text)
        if (!matcher.find()) return null

        val isNext = !matcher.group(1).isNullOrBlank()
        val weekday = weekdayLookup[matcher.group(2)?.lowercase(Locale.ENGLISH)] ?: return null
        val calendar = calendarAt(nowMillis)
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        var delta = (weekday - currentDay + 7) % 7
        if (delta == 0 || isNext) {
            delta = if (delta == 0) 7 else delta + 7
        }
        calendar.add(Calendar.DAY_OF_YEAR, delta)
        applyTime(calendar, matcher.group(3))
        return calendar.timeInMillis
    }

    private fun parseTimeOnly(text: String, nowMillis: Long): Long? {
        val matcher = timeOnlyPattern.matcher(text)
        if (!matcher.find()) return null
        val calendar = calendarAt(nowMillis)
        applyTime(calendar, matcher.group(1))
        if (calendar.timeInMillis < nowMillis && abs(calendar.timeInMillis - nowMillis) > 60_000L) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }

    private fun buildExplicitDate(
        nowMillis: Long,
        providedYear: Int?,
        month: Int,
        day: Int,
        timeText: String?
    ): Long? {
        val now = calendarAt(nowMillis)
        val candidate = calendarAt(nowMillis)
        candidate.set(Calendar.MONTH, month)
        candidate.set(Calendar.DAY_OF_MONTH, day)
        candidate.set(Calendar.YEAR, providedYear ?: now.get(Calendar.YEAR))
        applyTime(candidate, timeText)

        if (providedYear == null && candidate.timeInMillis < nowMillis) {
            candidate.add(Calendar.YEAR, 1)
        }
        return candidate.timeInMillis
    }

    private fun applyTime(calendar: Calendar, timeText: String?) {
        if (timeText.isNullOrBlank()) {
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return
        }

        val normalized = timeText.trim().lowercase(Locale.ENGLISH)
        val matcher = Regex("(\\d{1,2}):(\\d{2})(?:\\s*(am|pm))?").matchEntire(normalized) ?: run {
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return
        }

        var hour = matcher.groupValues[1].toInt()
        val minute = matcher.groupValues[2].toInt()
        val meridiem = matcher.groupValues.getOrNull(3)

        if (meridiem == "pm" && hour < 12) hour += 12
        if (meridiem == "am" && hour == 12) hour = 0

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    private fun calendarAt(timeMillis: Long): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = timeMillis
        }
    }
}
