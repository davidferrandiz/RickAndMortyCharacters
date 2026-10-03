package com.davidferrandiz.rickandmortycharacters.core.ui.component

import androidx.annotation.StringRes
import com.davidferrandiz.rickandmortycharacters.core.ui.R
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender

@get:StringRes
val Gender.labelRes: Int
    get() = when (this) {
        Gender.Female -> R.string.gender_female
        Gender.Male -> R.string.gender_male
        Gender.Genderless -> R.string.gender_genderless
        Gender.Unknown -> R.string.unknown
    }
