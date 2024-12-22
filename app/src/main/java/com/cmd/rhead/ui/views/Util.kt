package com.cmd.rhead.ui.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDialog(
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    onOk: () -> Unit
) {
    DialogContainer(
        title = title,
        icon = Icons.Default.Warning,
        onDismissRequest = onDismissRequest,
    ) {
        Text(text = description)
        DialogButtonLine {
            TextButton(onClick = onDismissRequest) { Text(text = "Cancel") }
            TextButton(onClick = {
                onOk()
                onDismissRequest()
            }) { Text(text = "Ok") }
        }
    }
}
