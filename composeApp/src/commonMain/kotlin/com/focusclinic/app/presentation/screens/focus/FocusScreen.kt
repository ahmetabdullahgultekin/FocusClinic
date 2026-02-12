package com.focusclinic.app.presentation.screens.focus

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.focusclinic.app.platform.AppLifecycleObserver
import com.focusclinic.app.presentation.Strings
import com.focusclinic.domain.valueobject.FocusDuration

@Composable
fun FocusScreen(viewModel: FocusViewModel) {
    val state by viewModel.state.collectAsState()

    AppLifecycleObserver(
        onBackground = { viewModel.onIntent(FocusIntent.AppBackgrounded) },
        onForeground = { viewModel.onIntent(FocusIntent.AppResumed) },
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = state.phase,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
            contentKey = { it::class },
        ) { phase ->
            when (phase) {
                FocusPhase.Idle -> IdleContent(
                    state = state,
                    onSelectDuration = { viewModel.onIntent(FocusIntent.SelectDuration(it)) },
                    onStart = { viewModel.onIntent(FocusIntent.StartSession) },
                )
                FocusPhase.Focusing -> FocusingContent(
                    state = state,
                    onCancel = { viewModel.onIntent(FocusIntent.CancelSession) },
                )
                FocusPhase.Completed, FocusPhase.Interrupted -> ResultContent(
                    state = state,
                    onDismiss = { viewModel.onIntent(FocusIntent.DismissResult) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IdleContent(
    state: FocusState,
    onSelectDuration: (FocusDuration) -> Unit,
    onStart: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = Strings.FOCUS_PATIENT_WAITING,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(8.dp))

        PatientVisual(
            phase = FocusPhase.Idle,
            modifier = Modifier.size(120.dp),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = Strings.FOCUS_SELECT_DURATION,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            state.availableDurations.forEach { duration ->
                DurationChip(
                    duration = duration,
                    isSelected = duration == state.selectedDuration,
                    onClick = { onSelectDuration(duration) },
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        BalanceRow(balance = state.balance.amount)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(
                text = Strings.FOCUS_START,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun FocusingContent(
    state: FocusState,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = Strings.FOCUS_PATIENT_TREATING,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(16.dp))

        PatientVisual(
            phase = FocusPhase.Focusing,
            modifier = Modifier.size(100.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        TimerRing(
            progress = state.progress,
            timerText = state.timerText,
            modifier = Modifier.size(220.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = Strings.FOCUS_TIME_REMAINING,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = onCancel,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error,
            ),
        ) {
            Text(Strings.FOCUS_CANCEL)
        }
    }
}

@Composable
private fun ResultContent(
    state: FocusState,
    onDismiss: () -> Unit,
) {
    val result = state.sessionResult ?: return
    val isCompleted = state.phase == FocusPhase.Completed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn(tween(400)) + fadeIn(),
        ) {
            PatientVisual(
                phase = state.phase,
                modifier = Modifier.size(120.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isCompleted) Strings.FOCUS_PATIENT_HAPPY else Strings.FOCUS_PATIENT_ANGRY,
            style = MaterialTheme.typography.titleMedium,
            color = if (isCompleted) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error,
        )

        Spacer(modifier = Modifier.height(24.dp))

        ResultCard(
            title = if (isCompleted) Strings.FOCUS_COMPLETED_TITLE else Strings.FOCUS_INTERRUPTED_TITLE,
            earnedXp = result.earnedXp.value,
            earnedCoins = result.earnedCoins.amount,
            isCompleted = isCompleted,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(
                text = Strings.FOCUS_CONTINUE,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun TimerRing(
    progress: Float,
    timerText: String,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
    )
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier.semantics { contentDescription = Strings.CD_TIMER },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val padding = strokeWidth / 2
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)

            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(padding, padding),
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )

            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(padding, padding),
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }

        Text(
            text = timerText,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun PatientVisual(
    phase: FocusPhase,
    modifier: Modifier = Modifier,
) {
    val emoji = when (phase) {
        FocusPhase.Idle -> "\uD83E\uDD37"        // shrug — waiting
        FocusPhase.Focusing -> "\uD83E\uDE7A"     // face with bandage — being treated
        FocusPhase.Completed -> "\uD83D\uDE04"     // grinning face — happy
        FocusPhase.Interrupted -> "\uD83D\uDE21"   // angry face
    }

    Box(
        modifier = modifier.semantics { contentDescription = Strings.CD_PATIENT },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DurationChip(
    duration: FocusDuration,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text("${duration.minutes} ${Strings.FOCUS_MINUTES_SUFFIX}")
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}

@Composable
private fun BalanceRow(balance: Long) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "\uD83E\uDDB7",  // tooth emoji as coin placeholder
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${Strings.FOCUS_BALANCE}: $balance",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ResultCard(
    title: String,
    earnedXp: Long,
    earnedCoins: Long,
    isCompleted: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (isCompleted) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                RewardItem(
                    label = Strings.FOCUS_EARNED_XP,
                    value = "+$earnedXp",
                    icon = "\u2B50",  // star
                )
                RewardItem(
                    label = Strings.FOCUS_EARNED_COINS,
                    value = "+$earnedCoins",
                    icon = "\uD83E\uDDB7",  // tooth
                )
            }
        }
    }
}

@Composable
private fun RewardItem(
    label: String,
    value: String,
    icon: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
