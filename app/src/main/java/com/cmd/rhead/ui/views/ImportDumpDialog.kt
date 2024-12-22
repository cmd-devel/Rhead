package com.cmd.rhead.ui.views

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.cmd.rhead.viewmodels.ImportDumpDialogViewModel

@Composable
fun ImportDumpDialog(
    importDumpDialogViewModel: ImportDumpDialogViewModel,
    onTerminate: () -> Unit
) {
    val isLoading by importDumpDialogViewModel.isLoading.collectAsState()
    val message by importDumpDialogViewModel.message.collectAsState()

    ImportDumpDialog(
        isLoading = isLoading,
        message = message,
        onTerminate = onTerminate,
    )
}

@Composable
fun ImportDumpDialog(isLoading: Boolean, message: String, onTerminate: () -> Unit) {
    DialogContainer(
        title = "Import",
        onDismissRequest = onTerminate
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = message)
            DialogButtonLine {
                TextButton(onClick = onTerminate) { Text(text = "Close") }
            }
        }
    }
}
