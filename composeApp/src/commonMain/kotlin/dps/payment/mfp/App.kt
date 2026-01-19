package dps.payment.mfp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview
fun App() {
    MaterialTheme {
        PasswordGeneratorScreen()
    }
}

@Composable
fun PasswordGeneratorScreen() {
    var dateInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var generatedPassword by remember { mutableStateOf<String?>(null) }

    val isValidDate = remember(dateInput) {
        dateInput.isNotBlank() && DateParser.parseDate(dateInput) != null
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
                generatedPassword = null
            },
            label = { Text("Date") },
            placeholder = { Text("yyyy-MM-dd, dd-MM-yyyy, or dd/MM/yyyy") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = errorMessage != null,
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

        // Generate button
        Button(
            onClick = {
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
                    errorMessage = "Please enter a valid date in a supported format"
                    generatedPassword = null
                }
            },
            enabled = isValidDate,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Generate Password",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

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
                    Text(
                        text = "Generated Password",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )

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