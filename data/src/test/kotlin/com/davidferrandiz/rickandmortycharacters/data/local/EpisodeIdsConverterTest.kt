package com.davidferrandiz.rickandmortycharacters.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class EpisodeIdsConverterTest {

    private val converter = EpisodeIdsConverter()

    @Test
    fun `ids survive the round trip through the column`() {
        val ids = listOf(1, 27, 51)

        assertEquals(ids, converter.fromColumn(converter.toColumn(ids)))
    }

    @Test
    fun `an empty column is an empty list and not a parsing crash`() {
        assertEquals("", converter.toColumn(emptyList()))
        assertEquals(emptyList<Int>(), converter.fromColumn(""))
    }
}
