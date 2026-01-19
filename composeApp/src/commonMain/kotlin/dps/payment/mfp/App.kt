package dps.payment.mfp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import dps.payment.mfp.composeapp.generated.resources.Res
import dps.payment.mfp.composeapp.generated.resources.app_title
import dps.payment.mfp.composeapp.generated.resources.app_subtitle
import dps.payment.mfp.composeapp.generated.resources.cancel
import dps.payment.mfp.composeapp.generated.resources.copy_password
import dps.payment.mfp.composeapp.generated.resources.daily_password_title
import dps.payment.mfp.composeapp.generated.resources.date_label
import dps.payment.mfp.composeapp.generated.resources.date_placeholder
import dps.payment.mfp.composeapp.generated.resources.error_generating_password
import dps.payment.mfp.composeapp.generated.resources.how_it_works_body
import dps.payment.mfp.composeapp.generated.resources.how_it_works_title
import dps.payment.mfp.composeapp.generated.resources.invalid_date_format
import dps.payment.mfp.composeapp.generated.resources.ok
import dps.payment.mfp.composeapp.generated.resources.select_date
import dps.payment.mfp.composeapp.generated.resources.supported_formats
import dps.payment.mfp.composeapp.generated.resources.tap_to_copy

@Composable
@Preview
fun App() {
    AppTheme {
        PasswordGeneratorScreen()
    }
}

@Composable
private fun AppTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) {
        darkColorScheme(
            primary = Color(0xFF8AB4F8),
            onPrimary = Color(0xFF0B1D3A),
            primaryContainer = Color(0xFF1E4F91),
            onPrimaryContainer = Color(0xFFEAF1FF),
            secondary = Color(0xFF9AB3D6),
            onSecondary = Color(0xFF102238),
            background = Color(0xFF0E1116),
            onBackground = Color(0xFFE6EAF2),
            surface = Color(0xFF141821),
            onSurface = Color(0xFFE6EAF2),
            surfaceVariant = Color(0xFF1E2532),
            onSurfaceVariant = Color(0xFFC6D4E3),
            error = Color(0xFFF2B8B5),
            onError = Color(0xFF601410)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF1A73E8),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFF3B82F6),
            onPrimaryContainer = Color(0xFFFFFFFF),
            secondary = Color(0xFF4B6B9A),
            onSecondary = Color(0xFFFFFFFF),
            background = Color(0xFFF6F8FC),
            onBackground = Color(0xFF1A1C20),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1A1C20),
            surfaceVariant = Color(0xFFE7EEF8),
            onSurfaceVariant = Color(0xFF2B3548),
            error = Color(0xFFB3261E),
            onError = Color(0xFFFFFFFF)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
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
                errorMessage = e.message ?: ""
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
                    Text(stringResource(Res.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(Res.string.cancel))
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
            text = stringResource(Res.string.app_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Text(
            text = stringResource(Res.string.app_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        // Daily password
        generatedPassword?.let { password ->
            DailyPasswordCard(
                password = password,
                onCopy = { clipboardManager.setText(AnnotatedString(password)) }
            )
        }

        // Date input field
        OutlinedTextField(
            value = dateInput,
            onValueChange = { newValue ->
                dateInput = newValue
                errorMessage = null
            },
            label = { Text(stringResource(Res.string.date_label)) },
            placeholder = { Text(stringResource(Res.string.date_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = errorMessage != null || (dateInput.isNotBlank() && !isValidDate),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = stringResource(Res.string.select_date)
                    )
                }
            },
            supportingText = {
                if (errorMessage != null) {
                    Text(
                        text = stringResource(
                            Res.string.error_generating_password,
                            errorMessage ?: ""
                        ),
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (dateInput.isNotBlank() && !isValidDate) {
                    Text(
                        text = stringResource(Res.string.invalid_date_format),
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(stringResource(Res.string.supported_formats))
                }
            }
        )

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
                    text = stringResource(Res.string.how_it_works_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(Res.string.how_it_works_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

private fun todayDate(): SimpleDate {
    return getTodayDate()
}

private fun SimpleDate.toInputString(): String {
    val monthString = month.toString().padStart(2, '0')
    val dayString = day.toString().padStart(2, '0')
    return "$year-$monthString-$dayString"
}

@Composable
private fun DailyPasswordCard(
    password: String,
    onCopy: () -> Unit
) {
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
                .clickable { onCopy() }
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
                    text = stringResource(Res.string.daily_password_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = onCopy) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = stringResource(Res.string.copy_password)
                    )
                }
            }

            Text(
                text = password,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                letterSpacing = 4.sp
            )

            Text(
                text = stringResource(Res.string.tap_to_copy),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}
