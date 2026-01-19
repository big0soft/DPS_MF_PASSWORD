package dps.payment.mfp.cli

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import java.util.Locale

object PasswordConsole {
    private val inputFormats = listOf(
        DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT),
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val date = promptForDate()
        val password = PasswordGenerator.generatePassword(date)
        println("Password: $password")
    }

    private fun promptForDate(): LocalDate {
        while (true) {
            print("Enter date (yyyy-MM-dd, dd-MM-yyyy, or dd/MM/yyyy): ")
            val input = readLine()?.trim().orEmpty()
            val date = parseDate(input)
            if (date != null) {
                return date
            }
            println("Invalid date. Please enter a real calendar date in a supported format.")
        }
    }

    private fun parseDate(input: String): LocalDate? {
        if (input.isBlank()) {
            return null
        }
        for (formatter in inputFormats) {
            try {
                return LocalDate.parse(input, formatter)
            } catch (_: DateTimeParseException) {
                // Try next format.
            }
        }
        return null
    }
}

object PasswordGenerator {
    fun generatePassword(date: LocalDate): String {
        val day = date.dayOfMonth
        val month = date.monthValue
        val year = date.year % 100
        val base = String.format(Locale.US, "%02d%02d%02d", day, month, year)
        require(base.length == 6) { "Expected 6-digit base string, got: $base" }

        val reversed = base.reversed()
        require(reversed.length == 6) { "Expected 6-digit reversed string, got: $reversed" }

        val password = incrementDigits(reversed)
        require(password.length == 6) { "Expected 6-digit password, got: $password" }
        return password
    }

    private fun incrementDigits(input: String): String {
        return buildString(input.length) {
            input.forEachIndexed { index, c ->
                val digit = c.digitToIntOrNull()
                    ?: throw IllegalArgumentException("Non-digit character found: $c")
                val increment = if (index % 2 == 0) 2 else 3
                append((digit + increment) % 10)
            }
        }
    }
}
