package com.cmd.rhead.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.cmd.rhead.ui.views.DialogButtonLine
import com.cmd.rhead.ui.views.DialogContainer

@Composable
fun SimpleMessageDialog(
    icon: ImageVector,
    title: String,
    message: String,
    onDismissRequest: () -> Unit
) {
    DialogContainer(
        title = title,
        icon = icon,
        onDismissRequest = onDismissRequest,
    ) {
        Column {
            Text(
                text = message,
                modifier = Modifier.padding(start = 16.dp),
            )
            DialogButtonLine {
                TextButton(onClick = onDismissRequest) { Text(text = "Close") }
            }
        }
    }
}