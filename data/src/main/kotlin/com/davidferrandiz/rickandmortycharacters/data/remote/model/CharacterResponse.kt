package com.davidferrandiz.rickandmortycharacters.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
internal data class CharacterResponse(
    val id: Int,
    val name: String,
    val status: String = "",
    val species: String = "",
    val type: String = "",
    val gender: String = "",
    val origin: PlaceResponse = PlaceResponse(),
    val location: PlaceResponse = PlaceResponse(),
    val image: String = "",
    val episode: List<String> = emptyList(),
)

@Serializable
internal data class PlaceResponse(
    val name: String = "",
)
