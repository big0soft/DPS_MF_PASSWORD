package dps.payment.mfp

expect fun getTodayDate(): SimpleDate
expect fun SimpleDate.toEpochMillisAtStartOfDay(): Long
expect fun millisToSimpleDate(millis: Long): SimpleDate