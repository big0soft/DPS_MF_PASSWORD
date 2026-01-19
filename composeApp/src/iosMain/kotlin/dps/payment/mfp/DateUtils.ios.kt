package dps.payment.mfp

import kotlinx.datetime.*
import platform.Foundation.NSDate
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSCalendarUnitCalendar

actual fun getTodayDate(): SimpleDate {
    val calendar = NSCalendar.currentCalendar
    val components = calendar.components(
        NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
        fromDate = NSDate()
    )
    return SimpleDate(
        year = components.year.toInt(),
        month = components.month.toInt(),
        day = components.day.toInt()
    )
}

actual fun SimpleDate.toEpochMillisAtStartOfDay(): Long {
    val localDate = LocalDate(year, month, day)
    val instant = localDate.atStartOfDayIn(TimeZone.currentSystemDefault())
    return instant.toEpochMilliseconds()
}

actual fun millisToSimpleDate(millis: Long): SimpleDate {
    val instant = Instant.fromEpochMilliseconds(millis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val date = localDateTime.date
    return SimpleDate(
        year = date.year,
        month = date.monthNumber,
        day = date.dayOfMonth
    )
}
