package dps.payment.mfp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@Composable
@Preview
fun App() {
    MaterialTheme {
        PasswordGeneratorScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordGeneratorScreen() {
    val today = remember { todayDate() }
    var dateInput by remember { mutableStateOf(today.toInputString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var generatedPassword by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    val isValidDate = remember(dateInput) {
        dateInput.isNotBlank() && DateParser.parseDate(dateInput) != null
    }

    LaunchedEffect(dateInput) {
        val date = DateParser.parseDate(dateInput)
        if (date != null) {
            try {
                generatedPassword = PasswordGenerator.generatePassword(date)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Error generating password: ${e.message}"
                generatedPassword = null
            }
        } else {
            if (dateInput.isBlank()) {
                errorMessage = null
            }
            generatedPassword = null
        }
    }

    if (showDatePicker) {
        val initialDate = DateParser.parseDate(dateInput) ?: today
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDate.toEpochMillisAtStartOfDay()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = millisToSimpleDate(millis)
                            dateInput = selectedDate.toInputString()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title
        Text(
            text = "Password Generator",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Text(
            text = "Enter a date to generate a password",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        // Date input field
        OutlinedTextField(
            value = dateInput,
            onValueChange = { newValue ->
                dateInput = newValue
                errorMessage = null
            },
            label = { Text("Date") },
            placeholder = { Text("yyyy-MM-dd, dd-MM-yyyy, or dd/MM/yyyy") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = errorMessage != null || (dateInput.isNotBlank() && !isValidDate),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date"
                    )
                }
            },
            supportingText = {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (dateInput.isNotBlank() && !isValidDate) {
                    Text(
                        text = "Invalid date format",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("Supported formats: yyyy-MM-dd, dd-MM-yyyy, dd/MM/yyyy")
                }
            }
        )

        // Generated password display
        generatedPassword?.let { password ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Generated Password",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(password))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy password"
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = password,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            letterSpacing = 4.sp
                        )
                    }
                }
            }
        }

        // Info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "How it works:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "1. Enter a date in any supported format\n" +
                            "2. The date is converted to DDMMYY format\n" +
                            "3. The digits are reversed\n" +
                            "4. Each digit is incremented (even positions +2, odd positions +3)\n" +
                            "5. Result is taken modulo 10",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

private fun todayDate(): SimpleDate {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    return SimpleDate(
        year = today.year,
        month = today.monthNumber,
        day = today.dayOfMonth
    )
}

private fun SimpleDate.toInputString(): String {
    val monthString = month.toString().padStart(2, '0')
    val dayString = day.toString().padStart(2, '0')
    return "$year-$monthString-$dayString"
}

private fun SimpleDate.toEpochMillisAtStartOfDay(): Long {
    val date = LocalDate(year = year, monthNumber = month, dayOfMonth = day)
    return date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}

private fun millisToSimpleDate(millis: Long): SimpleDate {
    val date = Instant.fromEpochMilliseconds(millis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    return SimpleDate(
        year = date.year,
        month = date.monthNumber,
        day = date.dayOfMonth
    )
}