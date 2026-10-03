package com.davidferrandiz.rickandmortycharacters.data.remote

import com.davidferrandiz.rickandmortycharacters.data.remote.model.PageInfoResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PageInfoTest {

    @Test
    fun `reads the next page from the url the API returns today`() {
        val info = PageInfoResponse(count = 826, next = "https://rickandmortyapi.com/api/character?page=2")

        assertEquals(2, info.nextPage)
    }

    @Test
    fun `reads the next page from the url shape the documentation shows`() {
        val info = PageInfoResponse(
            count = 29,
            next = "https://rickandmortyapi.com/api/character/?page=2&name=rick&status=alive",
        )

        assertEquals(2, info.nextPage)
    }

    @Test
    fun `there is no next page on the last page`() {
        assertNull(PageInfoResponse(count = 826, next = null).nextPage)
    }

    @Test
    fun `a malformed next url ends the pagination instead of crashing`() {
        assertNull(PageInfoResponse(count = 826, next = "not a url").nextPage)
        assertNull(PageInfoResponse(count = 826, next = "https://rickandmortyapi.com/api/character").nextPage)
    }
}
