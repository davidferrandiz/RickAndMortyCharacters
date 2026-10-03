package com.davidferrandiz.rickandmortycharacters.domain.model

data class CharacterFilter(
    val name: String = "",
    val status: CharacterStatus? = null,
    val gender: Gender? = null,
) {
    val isEmpty: Boolean
        get() = name.isBlank() && status == null && gender == null
}
