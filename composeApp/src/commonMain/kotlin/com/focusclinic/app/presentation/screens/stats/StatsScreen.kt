package com.focusclinic.app.presentation.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.focusclinic.app.presentation.Strings
import com.focusclinic.domain.model.FocusSession
import com.focusclinic.domain.model.SessionStatus

@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = Strings.STATS_TITLE,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }

        item { SummaryRow(state = state) }

        item { PlayerSummaryCard(state = state) }

        item {
            Text(
                text = Strings.STATS_SESSION_HISTORY,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        if (state.sessions.isEmpty()) {
            item { EmptyHistoryMessage() }
        } else {
            items(state.sessions, key = { it.id }) { session ->
                SessionCard(
                    session = session,
                    modifier = Modifier.animateItem(),
                )
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun SummaryRow(state: StatsState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SummaryCard(
            icon = "\uD83C\uDFAF",  // target
            value = state.totalSessions.toString(),
            label = Strings.STATS_TOTAL_SESSIONS,
            modifier = Modifier.weight(1f),
        )
        SummaryCard(
            icon = "\u2705",  // check
            value = state.completedSessions.toString(),
            label = Strings.STATS_COMPLETED,
            modifier = Modifier.weight(1f),
        )
        SummaryCard(
            icon = "\u23F1\uFE0F",  // stopwatch
            value = formatDuration(state.totalFocusMinutes),
            label = Strings.STATS_TOTAL_FOCUS,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SummaryCard(
    icon: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PlayerSummaryCard(state: StatsState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatItem(
                icon = "\u2B50",
                value = state.totalEarnedXp.toString(),
                label = Strings.STATS_TOTAL_XP,
            )

            VerticalDivider()

            StatItem(
                icon = "\u2728",
                value = state.totalEarnedCoins.toString(),
                label = Strings.STATS_TOTAL_COINS,
            )

            VerticalDivider()

            StatItem(
                icon = levelEmoji(state.playerLevel.level),
                value = "${Strings.PROFILE_LEVEL} ${state.playerLevel.level}",
                label = state.playerLevel.title,
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: String,
    value: String,
    label: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(48.dp)
            .padding(vertical = 4.dp),
    ) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxSize()
                .width(1.dp),
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f),
        )
    }
}

@Composable
private fun SessionCard(
    session: FocusSession,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = statusEmoji(session.status),
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${session.plannedDuration.minutes} ${Strings.FOCUS_MINUTES_SUFFIX} ${statusLabel(session.status)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "${session.actualFocusMinutes} ${Strings.FOCUS_MINUTES_SUFFIX} focused",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "\u2B50",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "+${session.earnedXp.value}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "\u2728",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "+${session.earnedCoins.amount}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = Strings.STATS_NO_SESSIONS,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

private fun formatDuration(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) {
        "$hours${Strings.STATS_HOURS_SUFFIX} $mins${Strings.STATS_MINUTES_SUFFIX}"
    } else {
        "$mins${Strings.STATS_MINUTES_SUFFIX}"
    }
}

private fun statusEmoji(status: SessionStatus): String = when (status) {
    SessionStatus.Completed -> "\u2705"       // green check
    SessionStatus.Interrupted -> "\u26A0\uFE0F"  // warning
    SessionStatus.Cancelled -> "\u274C"        // red X
    else -> "\u2753"                           // question mark
}

private fun statusLabel(status: SessionStatus): String = when (status) {
    SessionStatus.Completed -> "— ${Strings.STATS_STATUS_COMPLETED}"
    SessionStatus.Interrupted -> "— ${Strings.STATS_STATUS_INTERRUPTED}"
    SessionStatus.Cancelled -> "— ${Strings.STATS_STATUS_CANCELLED}"
    else -> ""
}

private fun levelEmoji(level: Int): String = when {
    level >= 10 -> "\uD83C\uDFC6"  // trophy
    level >= 7 -> "\uD83C\uDF1F"   // glowing star
    level >= 5 -> "\uD83D\uDC8E"   // gem
    level >= 3 -> "\uD83D\uDCAA"   // flexed biceps
    level >= 2 -> "\u26A1"          // lightning
    else -> "\uD83C\uDF31"          // seedling
}
