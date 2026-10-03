package com.davidferrandiz.rickandmortycharacters.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

internal const val CHARACTER_PAGING_ROW_ID = 0

@Entity(tableName = "character_paging")
internal data class CharacterPagingEntity(
    @PrimaryKey val id: Int = CHARACTER_PAGING_ROW_ID,
    val nextPage: Int?,
    val totalCount: Int,
    val updatedAt: Long,
)
