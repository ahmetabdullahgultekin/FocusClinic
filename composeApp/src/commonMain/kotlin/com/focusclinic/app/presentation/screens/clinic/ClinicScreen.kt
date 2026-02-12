package com.focusclinic.app.presentation.screens.clinic

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
import com.focusclinic.app.presentation.Strings
import com.focusclinic.domain.model.InventoryItem

@Composable
fun ClinicScreen(viewModel: ClinicViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ClinicRoomVisual(
            equipment = state.equipment,
            decorations = state.decorations,
        )

        Spacer(modifier = Modifier.height(20.dp))

        PlayerLevelCard(state = state)

        Spacer(modifier = Modifier.height(16.dp))

        MultiplierCard(state = state)

        Spacer(modifier = Modifier.height(16.dp))

        InventorySection(
            title = Strings.CLINIC_EQUIPMENT,
            items = state.equipment,
            emptyMessage = Strings.CLINIC_NO_EQUIPMENT,
        )

        Spacer(modifier = Modifier.height(12.dp))

        InventorySection(
            title = Strings.CLINIC_DECORATIONS,
            items = state.decorations,
            emptyMessage = Strings.CLINIC_NO_DECORATIONS,
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ClinicRoomVisual(
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
                text = Strings.CLINIC_TITLE,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (equipment.isEmpty() && decorations.isEmpty()) {
                Text(
                    text = "\uD83C\uDFE5",  // hospital
                    style = MaterialTheme.typography.displayLarge,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = Strings.CLINIC_EMPTY_ROOM,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            } else {
                ClinicItemsGrid(equipment = equipment, decorations = decorations)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClinicItemsGrid(
    equipment: List<InventoryItem>,
    decorations: List<InventoryItem>,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        equipment.forEach { item ->
            ClinicItemBadge(item = item)
        }
        decorations.forEach { item ->
            ClinicItemBadge(item = item)
        }
    }
}

@Composable
private fun ClinicItemBadge(item: InventoryItem) {
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
private fun PlayerLevelCard(state: ClinicState) {
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
                        text = "${Strings.CLINIC_LEVEL} ${state.playerLevel.level}",
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
                text = Strings.CLINIC_XP_PROGRESS,
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
                        text = "${state.xpToNextLevel} ${Strings.CLINIC_XP_TO_NEXT}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Text(
                        text = Strings.CLINIC_MAX_LEVEL,
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
private fun MultiplierCard(state: ClinicState) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = Strings.CLINIC_MULTIPLIERS,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                MultiplierItem(
                    label = Strings.CLINIC_XP_MULTIPLIER,
                    value = state.xpMultiplier.value,
                    icon = "\u2B50",
                )
                MultiplierItem(
                    label = Strings.CLINIC_COIN_MULTIPLIER,
                    value = state.coinMultiplier.value,
                    icon = "\uD83E\uDDB7",
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
    "ergonomic_chair" -> "\uD83E\uDE91"
    "led_lamp" -> "\uD83D\uDCA1"
    "sterilizer_pro" -> "\uD83E\uDDF4"
    "digital_xray" -> "\uD83E\uDE7B"
    "wall_paint_ocean" -> "\uD83C\uDF0A"
    "diploma_frame" -> "\uD83C\uDF93"
    "potted_plant" -> "\uD83C\uDF3F"
    "aquarium" -> "\uD83D\uDC20"
    else -> "\uD83D\uDCE6"
}

private fun levelEmoji(level: Int): String = when {
    level >= 10 -> "\uD83C\uDFC6"  // trophy
    level >= 7 -> "\uD83C\uDF1F"   // glowing star
    level >= 5 -> "\uD83D\uDC8E"   // gem
    level >= 3 -> "\uD83E\uDE7A"   // stethoscope
    level >= 2 -> "\uD83D\uDC69\u200D\u2695\uFE0F"  // doctor
    else -> "\uD83D\uDC68\u200D\uD83C\uDF93"  // student
}
