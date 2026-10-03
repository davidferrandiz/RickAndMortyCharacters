package com.davidferrandiz.rickandmortycharacters.data.local

import androidx.room.TypeConverter

private const val SEPARATOR = ","

internal class EpisodeIdsConverter {

    @TypeConverter
    fun toColumn(ids: List<Int>): String = ids.joinToString(SEPARATOR)

    @TypeConverter
    fun fromColumn(column: String): List<Int> =
        if (column.isEmpty()) emptyList() else column.split(SEPARATOR).map(String::toInt)
}
