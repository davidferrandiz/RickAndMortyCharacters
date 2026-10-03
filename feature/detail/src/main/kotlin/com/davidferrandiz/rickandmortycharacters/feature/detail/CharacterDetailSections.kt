package com.davidferrandiz.rickandmortycharacters.feature.detail

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidferrandiz.rickandmortycharacters.core.ui.component.StatusDot
import com.davidferrandiz.rickandmortycharacters.core.ui.component.labelRes
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppShapes
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.AppTheme
import com.davidferrandiz.rickandmortycharacters.domain.model.Character
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus
import com.davidferrandiz.rickandmortycharacters.core.ui.R as CoreUiR

private const val FACT_COLUMNS = 2

@Composable
internal fun DetailHeader(character: Character, modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(CoreUiR.string.character_code, character.id),
                style = AppTheme.typography.codeLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            StatusPill(character.status)
        }
        Text(
            text = character.name,
            style = AppTheme.typography.detailName,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun StatusPill(status: CharacterStatus, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        modifier = modifier
            .height(30.dp)
            .background(MaterialTheme.colorScheme.surface, AppShapes.Pill)
            .border(1.dp, MaterialTheme.colorScheme.outline, AppShapes.Pill)
            .padding(horizontal = 12.dp),
    ) {
        StatusDot(status)
        Text(
            text = stringResource(status.labelRes),
            style = AppTheme.typography.meta.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
internal fun DetailFacts(character: Character, modifier: Modifier = Modifier) {
    val facts = buildList {
        add(R.string.detail_species to character.species)
        add(R.string.detail_gender to stringResource(character.gender.labelRes))
        character.type?.let { add(R.string.detail_type to it) }
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = modifier) {
        facts.chunked(FACT_COLUMNS).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (label, value) ->
                    FactCard(label = stringResource(label), value = value, modifier = Modifier.weight(1f))
                }
                repeat(FACT_COLUMNS - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun FactCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, AppShapes.Panel)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = label.uppercase(),
            style = AppTheme.typography.factLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = AppTheme.typography.factValue,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
internal fun DetailPlaces(character: Character, modifier: Modifier = Modifier) {
    val unknown = stringResource(CoreUiR.string.unknown)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, AppShapes.Panel)
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        PlaceRow(
            icon = R.drawable.ic_globe,
            label = stringResource(R.string.detail_origin),
            value = character.origin ?: unknown,
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AppTheme.colors.divider),
        )
        PlaceRow(
            icon = R.drawable.ic_pin,
            label = stringResource(R.string.detail_location),
            value = character.location ?: unknown,
        )
    }
}

@Composable
private fun PlaceRow(@DrawableRes icon: Int, label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.padding(vertical = 14.dp),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(20.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                style = AppTheme.typography.meta,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = AppTheme.typography.rowValue,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
