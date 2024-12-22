package com.cmd.rhead.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.cmd.rhead.datasource.ElementLocalDataSource
import com.cmd.rhead.getAppDatabase
import com.cmd.rhead.repository.AbstractElementRepository
import com.cmd.rhead.repository.ElementRepository
import com.cmd.rhead.ui.theme.DefaultWidgetPadding
import com.cmd.rhead.ui.theme.RheadTheme
import com.cmd.rhead.ui.views.DialogButtonLine
import com.cmd.rhead.ui.views.DialogContainer
import com.cmd.rhead.ui.views.ImportDumpDialog
import com.cmd.rhead.ui.views.NewElementMenu
import com.cmd.rhead.ui.widgets.SimpleMessageDialog
import com.cmd.rhead.viewmodels.ImportDumpDialogViewModel
import com.cmd.rhead.viewmodels.ImportType
import com.cmd.rhead.viewmodels.ImportViewModel
import com.cmd.rhead.viewmodels.NewElementInitialValues
import com.cmd.rhead.viewmodels.NewElementViewModel

class ImportActivity : ComponentActivity() {
    private val db = lazy {
        getAppDatabase(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val elementDataSource =
            ElementLocalDataSource(db.value.elementDao())
        val elementRepository = ElementRepository(elementDataSource)

        val importViewModel = ImportViewModel()
        val onTerminate = { finishAndRemoveTask() }

        var noDataToImport = false

        val data = intent.clipData
        if (data == null || data.itemCount == 0) {
            noDataToImport = true;
            return
        }

        val dataString = data.getItemAt(0)!!.text.toString()

        enableEdgeToEdge()
        setContent {
            val showImportTypeDialog by importViewModel.showImportTypeDialog.collectAsState()
            val importType by importViewModel.importType.collectAsState()

            RheadTheme {
                if (noDataToImport) {
                    SimpleMessageDialog(
                        icon = Icons.Default.Info,
                        title = "Import error",
                        message = "The data to import is either invalid or empty.",
                        onDismissRequest = onTerminate,
                    )
                } else {
                    if (showImportTypeDialog) {
                        ImportTypeDialog(
                            importType,
                            onImportTypeSelected = { importViewModel.onImportTypeSelected(it) },
                            onValidate = { importViewModel.onValidate() },
                            onTerminate = onTerminate
                        )
                    } else {
                        when (importType) {
                            ImportType.SINGLE_ELEMENT -> {
                                ImportSingleElement(dataString, elementRepository, onTerminate)
                            }

                            ImportType.DUMP -> {
                                ImportDump(dataString, elementRepository, onTerminate)
                            }
                        }
                    }
                }
            }
        }
    }
}

private inline val ImportType.displayName: String
    get() {
        return when (this) {
            ImportType.SINGLE_ELEMENT -> "Single element"
            ImportType.DUMP -> "Encoded dump"
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImportTypeDialog(
    importType: ImportType,
    onImportTypeSelected: (ImportType) -> Unit,
    onValidate: () -> Unit,
    onTerminate: () -> Unit
) {
    DialogContainer(
        title = "Import",
        icon = Icons.Default.Add,
        onDismissRequest = onTerminate
    ) {
        Column(
            Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(
                DefaultWidgetPadding
            )
        ) {
            ImportType.entries.forEach { ty ->
                val selected = (importType == ty)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selected,
                            onClick = { onImportTypeSelected(ty) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selected,
                        onClick = null
                    )
                    Text(
                        text = ty.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
        DialogButtonLine {
            TextButton(onClick = onTerminate) { Text(text = "Cancel") }
            TextButton(onClick = onValidate) { Text(text = "Import") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImportSingleElement(
    location: String,
    elementRepository: AbstractElementRepository,
    onTerminate: () -> Unit
) {
    val initialValues = NewElementInitialValues(location = location)
    val newElementViewModel = NewElementViewModel(elementRepository, initialValues)

    ModalBottomSheet(onDismissRequest = onTerminate) {
        NewElementMenu(newElementViewModel) {
            onTerminate()
        }
    }
}

@Composable
private fun ImportDump(
    encoded: String,
    elementRepository: AbstractElementRepository,
    onTerminate: () -> Unit
) {
    val importDumpDialogViewModel = ImportDumpDialogViewModel(encoded, elementRepository)

    ImportDumpDialog(importDumpDialogViewModel, onTerminate)
}
