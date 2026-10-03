package com.davidferrandiz.rickandmortycharacters.data.mapper

import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterFilter
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender

internal val CharacterFilter.nameQuery: String?
    get() = name.trim().ifEmpty { null }

internal val CharacterFilter.statusQuery: String?
    get() = when (status) {
        CharacterStatus.Alive -> "alive"
        CharacterStatus.Dead -> "dead"
        CharacterStatus.Unknown -> "unknown"
        null -> null
    }

internal val CharacterFilter.genderQuery: String?
    get() = when (gender) {
        Gender.Female -> "female"
        Gender.Male -> "male"
        Gender.Genderless -> "genderless"
        Gender.Unknown -> "unknown"
        null -> null
    }
