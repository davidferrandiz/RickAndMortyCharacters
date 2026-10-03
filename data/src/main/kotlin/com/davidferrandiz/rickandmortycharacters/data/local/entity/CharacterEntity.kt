package com.davidferrandiz.rickandmortycharacters.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender

@Entity(tableName = "characters")
internal data class CharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val type: String?,
    val gender: Gender,
    val origin: String,
    val location: String,
    val imageUrl: String,
    val episodeIds: List<Int>,
)
