package com.cmd.rhead.ui.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.cmd.rhead.viewmodels.TopBarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    showMenu: Boolean,
    onMenuClick: () -> Unit,
    onMenuDismissRequest: () -> Unit,
    onSave: () -> Unit
) {
    TopAppBar(
        title = { Text(style = MaterialTheme.typography.headlineMedium, text = title) },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = "Actions"
                )
            }
            DropdownMenu(
                expanded = showMenu, onDismissRequest = onMenuDismissRequest
            ) {
                DropdownMenuItem(text = {
                    Row {
                        Icon(Icons.Default.Share, contentDescription = "Save")
                        Text("Save")
                    }
                }, onClick = onSave)
            }
        }
    )
}

@Composable
fun TopBar(topBarViewModel: TopBarViewModel) {
    val showMenu by topBarViewModel.showMenu.collectAsState()
    val context = LocalContext.current
    TopBar(
        title = topBarViewModel.title,
        showMenu = showMenu,
        onMenuClick = { topBarViewModel.onMenuClick() },
        onMenuDismissRequest = { topBarViewModel.onMenuDismissRequest() },
        onSave = { topBarViewModel.onSave { context.startActivity(it) } },
    )
}