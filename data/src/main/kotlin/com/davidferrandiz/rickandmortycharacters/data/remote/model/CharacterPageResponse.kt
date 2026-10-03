package com.davidferrandiz.rickandmortycharacters.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
internal data class CharacterPageResponse(
    val info: PageInfoResponse,
    val results: List<CharacterResponse>,
)

@Serializable
internal data class PageInfoResponse(
    val count: Int,
    val next: String? = null,
)
