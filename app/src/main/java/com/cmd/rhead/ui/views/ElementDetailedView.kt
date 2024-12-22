package com.cmd.rhead.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cmd.rhead.ui.theme.DefaultContainerPadding
import com.cmd.rhead.ui.theme.DefaultPadding
import com.cmd.rhead.ui.theme.DefaultWidgetPadding
import com.cmd.rhead.viewmodels.ElementDetailedViewViewModel
import kotlinx.coroutines.launch

@Composable
fun ElementDetailedView(
    modifier: Modifier,
    name: String,
    location: String,
    deleteConfirmDialogVisible: Boolean,
    onConfirmDialogCancelPressed: () -> Unit,
    onDeletePressed: () -> Unit,
    onDeleteConfirmation: () -> Unit,
    showError: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val uriHandler = LocalUriHandler.current
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(DefaultContainerPadding),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            shape = ShapeDefaults.ExtraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column {
                Surface(
                    shape = ShapeDefaults.ExtraLarge,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    Icon(
                        Icons.TwoTone.Info,
                        modifier = Modifier.padding(DefaultPadding),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        contentDescription = "Element main icon"
                    )
                }
                Column(
                    modifier = Modifier.padding(DefaultContainerPadding),
                    verticalArrangement = Arrangement.spacedBy(DefaultWidgetPadding)
                ) {
                    Text(
                        text = name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Text(
                        text = location,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        if (deleteConfirmDialogVisible) {
            ConfirmDialog(
                title = "Permanently delete?",
                description = "This element will be deleted",
                onDismissRequest = { onConfirmDialogCancelPressed() },
                onOk = {
                    scope.launch { onDeleteConfirmation() }
                }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DefaultContainerPadding)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    try {
                        uriHandler.openUri(location)
                    } catch (e: Exception) {
                        showError(e.message ?: "Unknown error")
                    }
                }) { Text("Open") }

            OutlinedButton(modifier = Modifier.weight(1f), onClick = { onDeletePressed() }) {
                Text("Delete")
            }
        }
    }
}

@Composable
fun ElementDetailedView(
    modifier: Modifier,
    showError: (String) -> Unit,
    elementDetailedViewViewModel: ElementDetailedViewViewModel,
) {
    val name by elementDetailedViewViewModel.name.collectAsState()
    val location by elementDetailedViewViewModel.location.collectAsState()
    val deleteConfirmDialogVisible by elementDetailedViewViewModel.deleteConfirmDialogVisible.collectAsState()

    ElementDetailedView(
        modifier = modifier,
        name = name,
        location = location,
        deleteConfirmDialogVisible = deleteConfirmDialogVisible,
        onConfirmDialogCancelPressed = { elementDetailedViewViewModel.onConfirmDialogCancelPressed() },
        onDeletePressed = { elementDetailedViewViewModel.onDeletePressed() },
        onDeleteConfirmation = { elementDetailedViewViewModel.onDeleteConfirmation() },
        showError = showError,
    )
}
