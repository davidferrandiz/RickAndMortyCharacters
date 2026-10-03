package com.davidferrandiz.rickandmortycharacters.data.mapper

import com.davidferrandiz.rickandmortycharacters.data.remote.model.EpisodeResponse
import com.davidferrandiz.rickandmortycharacters.domain.model.Episode

internal fun EpisodeResponse.toDomain(): Episode = Episode(
    id = id,
    name = name,
    code = episode,
)
