package com.davidferrandiz.rickandmortycharacters.core.ui.component

import androidx.annotation.StringRes
import com.davidferrandiz.rickandmortycharacters.core.ui.R
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender

@get:StringRes
val CharacterStatus.labelRes: Int
    get() = when (this) {
        CharacterStatus.Alive -> R.string.status_alive
        CharacterStatus.Dead -> R.string.status_dead
        CharacterStatus.Unknown -> R.string.status_unknown
    }

@get:StringRes
val Gender.labelRes: Int
    get() = when (this) {
        Gender.Female -> R.string.gender_female
        Gender.Male -> R.string.gender_male
        Gender.Genderless -> R.string.gender_genderless
        Gender.Unknown -> R.string.unknown
    }
