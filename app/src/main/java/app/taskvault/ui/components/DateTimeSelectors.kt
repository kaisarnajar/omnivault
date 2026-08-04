package app.taskvault.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DateTimeSelectors(
    dueDate: Long?,
    onDueDateChange: (Long) -> Unit,
) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "DUE DATE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                val dateFormatted = dueDate?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it)) } ?: "Select Date"

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = dateFormatted,
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                    )
                    Box(
                        modifier =
                            Modifier
                                .matchParentSize()
                                .clickable {
                                    val calendar = Calendar.getInstance()
                                    if (dueDate != null) calendar.timeInMillis = dueDate
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            val cal = Calendar.getInstance()
                                            if (dueDate != null) cal.timeInMillis = dueDate
                                            cal.set(year, month, dayOfMonth)
                                            onDueDateChange(cal.timeInMillis)
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH),
                                    ).show()
                                },
                    )
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "DUE TIME",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                val timeFormatted = dueDate?.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it)) } ?: "Select Time"

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = timeFormatted,
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(Icons.Outlined.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                    )
                    Box(
                        modifier =
                            Modifier
                                .matchParentSize()
                                .clickable {
                                    val calendar = Calendar.getInstance()
                                    if (dueDate != null) calendar.timeInMillis = dueDate
                                    TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            val cal = Calendar.getInstance()
                                            if (dueDate != null) cal.timeInMillis = dueDate
                                            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                            cal.set(Calendar.MINUTE, minute)
                                            onDueDateChange(cal.timeInMillis)
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true,
                                    ).show()
                                },
                    )
                }
            }
        }
    }
}
