package com.davidferrandiz.rickandmortycharacters.data.remote

import com.davidferrandiz.rickandmortycharacters.data.remote.model.PageInfoResponse
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

private const val PAGE_PARAMETER = "page"

internal val PageInfoResponse.nextPage: Int?
    get() = next?.toHttpUrlOrNull()?.queryParameter(PAGE_PARAMETER)?.toIntOrNull()
