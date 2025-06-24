package com.example.herometrics.navigation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.herometrics.api.armory.CharacterViewData

class SharedViewModel : ViewModel() {
    private val _selectedCharacter = mutableStateOf<CharacterViewData?>(null)
    val selectedCharacter: State<CharacterViewData?> = _selectedCharacter

    fun setSelectedCharacter(character: CharacterViewData) {
        _selectedCharacter.value = character
    }

    fun clearSelectedCharacter() {
        _selectedCharacter.value = null
    }
}