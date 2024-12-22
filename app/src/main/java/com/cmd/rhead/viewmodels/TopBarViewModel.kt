package com.cmd.rhead.viewmodels

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmd.rhead.repository.AbstractElementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TopBarViewModel(val title: String, private val elementRepository: AbstractElementRepository) :
    ViewModel() {
    private val showMenuState = MutableStateFlow(false)
    val showMenu: StateFlow<Boolean> = showMenuState

    fun onMenuClick() {
        showMenuState.value = true
    }

    fun onMenuDismissRequest() {
        showMenuState.value = false
    }

    fun onSave(cb: (Intent) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val dump = elementRepository.dump()
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, dump)
                type = "text/plain"
            }

            cb(intent)
        }
    }
}