package com.cmd.rhead.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import com.cmd.rhead.model.MaxPriority
import com.cmd.rhead.model.MinPriority
import com.cmd.rhead.ui.theme.DefaultPadding
import com.cmd.rhead.ui.theme.DefaultWidgetPadding
import com.cmd.rhead.ui.widgets.SimpleMessageDialog
import com.cmd.rhead.viewmodels.NewElementViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrioritySlider(
    value: Int,
    start: Int,
    end: Int,
    steps: Int,
    onValueChange: (Int) -> Unit
) {
    require(value >= MinPriority);
    require(value <= MaxPriority);
    Column(
        modifier = Modifier.padding(DefaultWidgetPadding),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Priority", style = MaterialTheme.typography.labelSmall)
        Slider(
            value = value.toFloat(),
            onValueChange = {
                onValueChange(it.roundToInt())
            },
            valueRange = start.toFloat()..end.toFloat(),
            steps = steps,
        )
    }
}

@Composable
fun NewElementMenu(newElementViewModel: NewElementViewModel, onCompleted: () -> Unit) {
    val name by newElementViewModel.name.collectAsState()
    val location by newElementViewModel.location.collectAsState()
    val priority by newElementViewModel.priority.collectAsState()
    val canValidate by newElementViewModel.canValidate.collectAsState()
    val errorMessage by newElementViewModel.errorMessage.collectAsState()
    val scope = rememberCoroutineScope()

    NewElementMenu(
        name = name,
        location = location,
        priority = priority,
        canValidate = canValidate,
        errorMessage = errorMessage,
        onErrorAcknowledged = { newElementViewModel.onErrorAcknowledged() },
        onNameChange = { newElementViewModel.onNameChange(it) },
        onLocationChange = { newElementViewModel.onLocationChange(it) },
        onPriorityChange = { newElementViewModel.onPriorityChange(it) },
        onValiation = {
            scope.launch {
                newElementViewModel.validate(onCompleted = onCompleted)
            }
        }
    )
}

@Composable
private fun NewElementMenu(
    name: String,
    location: String,
    priority: Int,
    canValidate: Boolean,
    errorMessage: String?,
    onErrorAcknowledged: () -> Unit,
    onNameChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onPriorityChange: (Int) -> Unit,
    onValiation: () -> Unit
) {
    val commonModifier = Modifier
        .fillMaxWidth()
        .padding(DefaultWidgetPadding);

    if (errorMessage != null) {
        SimpleMessageDialog(
            icon = Icons.Default.Warning,
            title = "Creation error",
            message = errorMessage,
            onDismissRequest = onErrorAcknowledged,
        )
    }

    Column(
        modifier = commonModifier.padding(DefaultPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = commonModifier,
            text = "New element",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = DefaultPadding))
        OutlinedTextField(
            modifier = commonModifier,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            singleLine = true,
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") }
        )
        OutlinedTextField(
            modifier = commonModifier,
            singleLine = true,
            value = location,
            onValueChange = onLocationChange,
            label = { Text("Location") }
        )

        PrioritySlider(
            value = priority,
            1,
            5,
            5,
            onPriorityChange
        )
        Button(
            modifier = commonModifier,
            onClick = onValiation,
            enabled = canValidate,
        ) {
            Text("Save")
        }
    }
}