package com.gsnamespace.atforecast.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Dialog for searching shelters by mileage marker.
 */
@Composable
fun MileageSearchDialog(
    onSearch: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var mileageText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Search by Mileage",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Enter a NOBO mile marker (0 - 2500)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = mileageText,
                    onValueChange = {
                        mileageText = it
                        errorMessage = null
                    },
                    label = { Text("Mile Marker") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            val mileage = mileageText.toDoubleOrNull()
                            when {
                                mileage == null -> errorMessage = "Please enter a valid number"
                                mileage < 0 || mileage > 2500 -> errorMessage =
                                    "Mileage must be between 0 and 2500"
                                else -> {
                                    onSearch(mileage)
                                    onDismiss()
                                }
                            }
                        }
                    ),
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val mileage = mileageText.toDoubleOrNull()
                    when {
                        mileage == null -> errorMessage = "Please enter a valid number"
                        mileage < 0 || mileage > 2500 -> errorMessage =
                            "Mileage must be between 0 and 2500"
                        else -> {
                            onSearch(mileage)
                            onDismiss()
                        }
                    }
                }
            ) {
                Text("Search")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
