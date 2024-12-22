package com.cmd.rhead.ui.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cmd.rhead.model.ElementId
import com.cmd.rhead.repository.AbstractElementRepository
import com.cmd.rhead.ui.widgets.ElementList
import com.cmd.rhead.ui.widgets.TopBar
import com.cmd.rhead.viewmodels.ElementDetailedViewViewModel
import com.cmd.rhead.viewmodels.ElementListViewModel
import com.cmd.rhead.viewmodels.NewElementViewModel
import com.cmd.rhead.viewmodels.TopBarViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object MainViewNav

@Serializable
data class ElementDetailsNav(val elementId: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    elementRepository: AbstractElementRepository,
    navController: NavHostController,
    elementListViewModel: ElementListViewModel,
    newElementViewModel: NewElementViewModel,
    topBarViewModel: TopBarViewModel,
) {
    val scope = rememberCoroutineScope()
    val showFabState = MutableStateFlow(true)
    val showFab by showFabState.collectAsState()
    var newElementMenuOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }

    val showMessageInSnackBar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    val lifeCycleOwner = LocalLifecycleOwner.current
    val lifeCycleState by lifeCycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifeCycleState) {
        when (lifeCycleState) {
            Lifecycle.State.RESUMED -> elementRepository.refresh()
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = {
                        newElementMenuOpen = true
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(Icons.Filled.Add, "FAB")
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = { TopBar(topBarViewModel) },
        content = { contentPadding ->
            NavHost(navController, startDestination = MainViewNav) {
                composable<MainViewNav> {
                    showFabState.value = true;
                    ElementList(elementListViewModel, modifier = Modifier.padding(contentPadding))
                    if (newElementMenuOpen) {
                        ModalBottomSheet(
                            sheetState = sheetState,
                            onDismissRequest = {
                                newElementMenuOpen = false
                                newElementViewModel.reset()
                            }
                        ) {
                            NewElementMenu(newElementViewModel) {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    newElementMenuOpen = false
                                    newElementViewModel.reset()
                                }
                            }
                        }
                    }
                }
                composable<ElementDetailsNav> { nbse ->
                    showFabState.value = false;
                    ElementDetailedView(
                        modifier = Modifier.padding(contentPadding),
                        elementDetailedViewViewModel = ElementDetailedViewViewModel(
                            elementRepository,
                            ElementId(nbse.toRoute<ElementDetailsNav>().elementId),
                            onDismissRequest = {
                                navController.popBackStack()
                            },
                        ),
                        showError = showMessageInSnackBar,
                    )
                }
            }
        }
    )
}
