package dps.payment.mfp

data class SimpleDate(
    val year: Int,
    val month: Int,
    val day: Int
) {
    val dayOfMonth: Int get() = day
    val monthValue: Int get() = month
}

object PasswordGenerator {
    fun generatePassword(date: SimpleDate): String {
        val day = date.dayOfMonth
        val month = date.monthValue
        val year = date.year % 100
        val base = day.toString().padStart(2, '0') +
                   month.toString().padStart(2, '0') +
                   year.toString().padStart(2, '0')
        require(base.length == 6) { "Expected 6-digit base string, got: $base" }

        val reversed = base.reversed()
        require(reversed.length == 6) { "Expected 6-digit reversed string, got: $reversed" }

        val password: String = incrementDigits(reversed)
        require(password.length == 6) { "Expected 6-digit password, got: $password" }
        return password
    }

    private fun incrementDigits(input: String): String {
        return buildString(input.length) {
            input.forEachIndexed { index, c ->
                val digit = c.digitToIntOrNull()
                    ?: throw IllegalArgumentException("Non-digit character found: $c")
                // Even indices (0, 2, 4): add 3, Odd indices (1, 3, 5): add 2
                val increment = if (index % 2 == 0) 3 else 2
                append((digit + increment) % 10)
            }
        }
    }
}

object DateParser {
    fun parseDate(input: String): SimpleDate? {
        if (input.isBlank()) {
            return null
        }

        // Try yyyy-MM-dd format
        val dashFormat1 = Regex("""^(\d{4})-(\d{2})-(\d{2})$""")
        dashFormat1.find(input)?.let { matchResult ->
            val (year, month, day) = matchResult.destructured
            val y = year.toIntOrNull() ?: return null
            val m = month.toIntOrNull() ?: return null
            val d = day.toIntOrNull() ?: return null
            if (isValidDate(y, m, d)) {
                return SimpleDate(y, m, d)
            }
        }

        // Try dd-MM-yyyy format
        val dashFormat2 = Regex("""^(\d{2})-(\d{2})-(\d{4})$""")
        dashFormat2.find(input)?.let { matchResult ->
            val (day, month, year) = matchResult.destructured
            val d = day.toIntOrNull() ?: return null
            val m = month.toIntOrNull() ?: return null
            val y = year.toIntOrNull() ?: return null
            if (isValidDate(y, m, d)) {
                return SimpleDate(y, m, d)
            }
        }

        // Try dd/MM/yyyy format
        val slashFormat = Regex("""^(\d{2})/(\d{2})/(\d{4})$""")
        slashFormat.find(input)?.let { matchResult ->
            val (day, month, year) = matchResult.destructured
            val d = day.toIntOrNull() ?: return null
            val m = month.toIntOrNull() ?: return null
            val y = year.toIntOrNull() ?: return null
            if (isValidDate(y, m, d)) {
                return SimpleDate(y, m, d)
            }
        }

        return null
    }

    private fun isValidDate(year: Int, month: Int, day: Int): Boolean {
        if (year < 1 || year > 9999) return false
        if (month < 1 || month > 12) return false
        if (day < 1) return false

        val daysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> return false
        }

        return day <= daysInMonth
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}
