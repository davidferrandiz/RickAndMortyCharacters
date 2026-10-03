package com.davidferrandiz.rickandmortycharacters.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.davidferrandiz.rickandmortycharacters.core.ui.R
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppShapes
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.RickAndMortyTheme
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus

@Composable
fun CharacterCard(
    id: Int,
    name: String,
    status: CharacterStatus,
    species: String,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(AppShapes.Card)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Box {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = imageModifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(AppTheme.colors.imagePlaceholder),
            )
            Text(
                text = stringResource(R.string.character_code, id),
                style = AppTheme.typography.code,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), AppShapes.Pill)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(start = 14.dp, top = 12.dp, end = 14.dp, bottom = 14.dp),
        ) {
            Text(
                text = name,
                style = AppTheme.typography.cardName,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusDot(status)
                Text(
                    text = stringResource(R.string.character_meta, stringResource(status.labelRes), species),
                    style = AppTheme.typography.meta,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CharacterCardPreview() {
    RickAndMortyTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
        ) {
            CharacterCard(
                id = 1,
                name = "Rick Sanchez",
                status = CharacterStatus.Alive,
                species = "Human",
                imageUrl = "",
                onClick = {},
                modifier = Modifier.width(169.dp),
            )
            CharacterCard(
                id = 7,
                name = "Abradolf Lincler",
                status = CharacterStatus.Unknown,
                species = "Human",
                imageUrl = "",
                onClick = {},
                modifier = Modifier.width(169.dp),
            )
        }
    }
}
