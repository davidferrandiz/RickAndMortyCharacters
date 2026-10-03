package com.davidferrandiz.rickandmortycharacters.data.mapper

import com.davidferrandiz.rickandmortycharacters.data.local.entity.CharacterEntity
import com.davidferrandiz.rickandmortycharacters.data.remote.model.CharacterResponse
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.domain.model.Gender

internal fun CharacterResponse.toEntity(): CharacterEntity = CharacterEntity(
    id = id,
    name = name,
    status = status.toCharacterStatus(),
    species = species,
    type = type.ifBlank { null },
    gender = gender.toGender(),
    origin = origin.name,
    location = location.name,
    imageUrl = image,
    episodeIds = episode.mapNotNull(String::toEpisodeId),
)

internal fun CharacterEntity.toDomain(): Character = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    origin = origin,
    location = location,
    imageUrl = imageUrl,
    episodeIds = episodeIds,
)

internal fun CharacterResponse.toDomain(): Character = toEntity().toDomain()

private fun String.toCharacterStatus(): CharacterStatus = when (lowercase()) {
    "alive" -> CharacterStatus.Alive
    "dead" -> CharacterStatus.Dead
    else -> CharacterStatus.Unknown
}

private fun String.toGender(): Gender = when (lowercase()) {
    "female" -> Gender.Female
    "male" -> Gender.Male
    "genderless" -> Gender.Genderless
    else -> Gender.Unknown
}

private fun String.toEpisodeId(): Int? = substringAfterLast('/').toIntOrNull()
