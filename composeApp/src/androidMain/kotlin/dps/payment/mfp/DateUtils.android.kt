package dps.payment.mfp

import java.time.LocalDate
import java.time.ZoneId

actual fun getTodayDate(): SimpleDate {
    val today = LocalDate.now()
    return SimpleDate(
        year = today.year,
        month = today.monthValue,
        day = today.dayOfMonth
    )
}

actual fun SimpleDate.toEpochMillisAtStartOfDay(): Long {
    val localDate = LocalDate.of(year, month, day)
    return localDate.atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

actual fun millisToSimpleDate(millis: Long): SimpleDate {
    val instant = java.time.Instant.ofEpochMilli(millis)
    val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    return SimpleDate(
        year = localDate.year,
        month = localDate.monthValue,
        day = localDate.dayOfMonth
    )
}
