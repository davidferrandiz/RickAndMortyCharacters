package com.davidferrandiz.rickandmortycharacters.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import com.davidferrandiz.rickandmortycharacters.core.ui.component.AppChip
import com.davidferrandiz.rickandmortycharacters.core.ui.component.CharacterCard
import com.davidferrandiz.rickandmortycharacters.core.ui.component.PrimaryButton
import com.davidferrandiz.rickandmortycharacters.core.ui.component.SearchField
import com.davidferrandiz.rickandmortycharacters.core.ui.component.SecondaryButton
import com.davidferrandiz.rickandmortycharacters.core.ui.component.StateMessage
import com.davidferrandiz.rickandmortycharacters.core.ui.component.TextAction
import com.davidferrandiz.rickandmortycharacters.core.ui.theme.RickAndMortyTheme
import com.davidferrandiz.rickandmortycharacters.domain.model.CharacterStatus

@Composable
private fun Surface(content: @Composable () -> Unit) {
    RickAndMortyTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
        ) {
            content()
        }
    }
}

@PreviewTest
@PreviewLightDark
@Composable
fun CharacterCards() {
    Surface {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                id = 8,
                name = "Adjudicator Rick",
                status = CharacterStatus.Dead,
                species = "Human",
                imageUrl = "",
                onClick = {},
                modifier = Modifier.width(169.dp),
            )
        }
        CharacterCard(
            id = 6,
            name = "Abadango Cluster Princess",
            status = CharacterStatus.Unknown,
            species = "Alien",
            imageUrl = "",
            onClick = {},
            modifier = Modifier.width(169.dp),
        )
    }
}

@PreviewTest
@PreviewLightDark
@Composable
fun Chips() {
    Surface {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppChip(label = "All", selected = true, onClick = {})
            AppChip(label = "Alive", selected = false, onClick = {})
            AppChip(label = "Dead", selected = false, onClick = {})
            AppChip(label = "Unknown", selected = false, onClick = {})
        }
    }
}

@PreviewTest
@PreviewLightDark
@Composable
fun SearchFields() {
    Surface {
        SearchField(query = "", onQueryChange = {}, modifier = Modifier.width(350.dp))
        SearchField(query = "morty", onQueryChange = {}, modifier = Modifier.width(350.dp))
    }
}

@PreviewTest
@PreviewLightDark
@Composable
fun Buttons() {
    Surface {
        PrimaryButton(text = "Apply filters", onClick = {}, modifier = Modifier.width(350.dp))
        SecondaryButton(text = "Clear search and filters", onClick = {})
        TextAction(text = "Retry", onClick = {}, underlined = true)
    }
}

@PreviewTest
@PreviewLightDark
@Composable
fun EmptyState() {
    RickAndMortyTheme {
        StateMessage(
            icon = R.drawable.ic_search_off,
            title = "No characters named “zara”",
            body = "Check the spelling or try another name. Not in this dimension, at least.",
            dashedIconBorder = true,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .height(520.dp),
        ) {
            SecondaryButton(text = "Clear search and filters", onClick = {})
        }
    }
}
