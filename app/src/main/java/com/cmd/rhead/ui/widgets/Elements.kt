package com.cmd.rhead.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmd.rhead.model.Element
import com.cmd.rhead.model.MaxPriority
import com.cmd.rhead.ui.theme.DefaultPadding
import com.cmd.rhead.ui.theme.ListBottomContentPadding
import com.cmd.rhead.viewmodels.ElementListSortMode
import com.cmd.rhead.viewmodels.ElementListViewModel
import java.text.SimpleDateFormat
import java.util.Date

private fun formatTimeStamp(timestamp: Long): String {
    val formatter = SimpleDateFormat.getDateInstance()
    return formatter.format(Date(timestamp))
}

@Composable
fun ElementListItem(element: Element, onClick: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(
                text = element.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(formatTimeStamp(element.timestamp))
        },
        trailingContent = {
            Badge(containerColor = MaterialTheme.colorScheme.secondary) {
                Text("${element.priority}/${MaxPriority}")
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${element.name.first().uppercaseChar()}",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
        },
        modifier = Modifier.clickable { onClick() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElementList(
    modifier: Modifier,
    list: List<Element>,
    sortMode: ElementListSortMode,
    onSortModeChange: (ElementListSortMode) -> Unit,
    onElementClick: (Int) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DefaultPadding)
        ) {
            ElementListSortMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = ElementListSortMode.entries.size
                    ),
                    onClick = { onSortModeChange(mode) },
                    selected = sortMode == mode
                ) {
                    Text(mode.sortModeName)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(0.dp)
                .fillMaxHeight(),
            contentPadding = PaddingValues(bottom = ListBottomContentPadding),
        ) {
            itemsIndexed(list, key = { _, e -> e.id.toString() }) { index, singleElementData ->
                Box(modifier = Modifier.animateItem()) {
                    ElementListItem(
                        singleElementData,
                        onClick = { onElementClick(index) })
                }
            }
        }
    }
}

@Composable
fun ElementList(elementListViewModel: ElementListViewModel, modifier: Modifier) {
    val list by elementListViewModel.list.collectAsState()
    val sortMode by elementListViewModel.sortMode.collectAsState()
    ElementList(
        modifier = modifier,
        list = list,
        sortMode = sortMode,
        onSortModeChange = { elementListViewModel.onSortModeChange(it) },
        onElementClick = { elementListViewModel.onElementClick(it) }
    )
}
