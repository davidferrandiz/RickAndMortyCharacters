package com.davidferrandiz.rickandmortycharacters.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
internal data class EpisodeResponse(
    val id: Int,
    val name: String,
    val episode: String = "",
)
