package com.focusclinic.app.presentation.screens.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.focusclinic.domain.model.InventoryItem
import focusclinic.composeapp.generated.resources.Res
import focusclinic.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProfileVisual(
            equipment = state.equipment,
            decorations = state.decorations,
        )

        Spacer(modifier = Modifier.height(20.dp))

        PlayerLevelCard(state = state)

        Spacer(modifier = Modifier.height(16.dp))

        MultiplierCard(state = state)

        Spacer(modifier = Modifier.height(16.dp))

        InventorySection(
            title = stringResource(Res.string.profile_tools),
            items = state.equipment,
            emptyMessage = stringResource(Res.string.profile_no_tools),
        )

        Spacer(modifier = Modifier.height(12.dp))

        InventorySection(
            title = stringResource(Res.string.profile_decorations),
            items = state.decorations,
            emptyMessage = stringResource(Res.string.profile_no_decorations),
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileVisual(
    equipment: List<InventoryItem>,
    decorations: List<InventoryItem>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.profile_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (equipment.isEmpty() && decorations.isEmpty()) {
                Text(
                    text = "\uD83D\uDD25",
                    style = MaterialTheme.typography.displayLarge,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.profile_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            } else {
                ProfileItemsGrid(equipment = equipment, decorations = decorations)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileItemsGrid(
    equipment: List<InventoryItem>,
    decorations: List<InventoryItem>,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        equipment.forEach { item ->
            ProfileItemBadge(item = item)
        }
        decorations.forEach { item ->
            ProfileItemBadge(item = item)
        }
    }
}

@Composable
private fun ProfileItemBadge(item: InventoryItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
    ) {
        Text(
            text = inventoryEmoji(item.itemId),
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PlayerLevelCard(state: ProfileState) {
    val animatedProgress by animateFloatAsState(
        targetValue = state.xpProgress,
        animationSpec = tween(durationMillis = 500),
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "${stringResource(Res.string.profile_level)} ${state.playerLevel.level}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = state.playerLevel.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
                Text(
                    text = levelEmoji(state.playerLevel.level),
                    style = MaterialTheme.typography.displaySmall,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(Res.string.profile_xp_progress),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "\u2B50 ${state.totalXp.value}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                )
                if (state.nextLevel != null) {
                    Text(
                        text = "${state.xpToNextLevel} ${stringResource(Res.string.profile_xp_to_next)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.profile_max_level),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun MultiplierCard(state: ProfileState) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.profile_multipliers),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                MultiplierItem(
                    label = stringResource(Res.string.profile_xp_multiplier),
                    value = state.xpMultiplier.value,
                    icon = "\u2B50",
                )
                MultiplierItem(
                    label = stringResource(Res.string.profile_coin_multiplier),
                    value = state.coinMultiplier.value,
                    icon = "\u2728",
                )
            }
        }
    }
}

@Composable
private fun MultiplierItem(
    label: String,
    value: Double,
    icon: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${formatMultiplier(value)}x",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun InventorySection(
    title: String,
    items: List<InventoryItem>,
    emptyMessage: String,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (items.isEmpty()) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = inventoryEmoji(item.itemId),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

private fun formatMultiplier(value: Double): String {
    val intPart = value.toLong()
    val fracPart = ((value - intPart) * 100).toLong()
    return "$intPart.${fracPart.toString().padStart(2, '0')}"
}

private fun inventoryEmoji(itemId: String): String = when (itemId) {
    "focus_stone" -> "\uD83D\uDD2E"
    "perseverance_shield" -> "\uD83D\uDEE1\uFE0F"
    "willpower_fire" -> "\uD83D\uDD25"
    "patience_medal" -> "\uD83C\uDFC5"
    "peace_garden" -> "\uD83C\uDF3B"
    "motivation_wall" -> "\uD83C\uDFA8"
    "inspiration_plant" -> "\uD83C\uDF3F"
    "victory_aquarium" -> "\uD83D\uDC20"
    else -> "\uD83D\uDCE6"
}

private fun levelEmoji(level: Int): String = when {
    level >= 10 -> "\uD83C\uDFC6"
    level >= 7 -> "\uD83C\uDF1F"
    level >= 5 -> "\uD83D\uDC8E"
    level >= 3 -> "\uD83D\uDCAA"
    level >= 2 -> "\u26A1"
    else -> "\uD83C\uDF31"
}
