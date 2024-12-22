package com.cmd.rhead.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.cmd.rhead.datasource.ElementLocalDataSource
import com.cmd.rhead.getAppDatabase
import com.cmd.rhead.repository.ElementRepository
import com.cmd.rhead.ui.theme.RheadTheme
import com.cmd.rhead.ui.views.ElementDetailsNav
import com.cmd.rhead.ui.views.MainView
import com.cmd.rhead.viewmodels.ElementListViewModel
import com.cmd.rhead.viewmodels.NewElementViewModel
import com.cmd.rhead.viewmodels.TopBarViewModel

class MainActivity : ComponentActivity() {
    private val db = lazy {
        getAppDatabase(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val elementDataSource = ElementLocalDataSource(db.value.elementDao())
            val elementRepository = ElementRepository(elementDataSource)
            val navController = rememberNavController()

            var newElementViewModel = NewElementViewModel(elementRepository)
            val elementListViewModel = ElementListViewModel(
                elementRepository,
                onElementClickNav = { navController.navigate(route = ElementDetailsNav(it.toString())) })

            val topBarViewModel = TopBarViewModel("To read", elementRepository)

            RheadTheme {
                MainView(
                    elementRepository,
                    navController,
                    elementListViewModel,
                    newElementViewModel,
                    topBarViewModel,
                )
            }
        }
    }
}

