package com.davidferrandiz.rickandmortycharacters.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object CharactersKey : NavKey

@Serializable
data class CharacterDetailKey(val characterId: Int, val imageUrl: String) : NavKey
