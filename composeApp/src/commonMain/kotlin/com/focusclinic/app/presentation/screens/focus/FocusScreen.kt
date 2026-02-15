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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import focusclinic.composeapp.generated.resources.Res
import focusclinic.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import com.focusclinic.app.presentation.components.CelebrationOverlay
import com.focusclinic.domain.model.WillpowerGoal
import com.focusclinic.domain.valueobject.FocusDuration

@Composable
fun FocusScreen(viewModel: FocusViewModel) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    AppLifecycleObserver(
        onBackground = { viewModel.onIntent(FocusIntent.AppBackgrounded) },
        onForeground = { viewModel.onIntent(FocusIntent.AppResumed) },
    )

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onIntent(FocusIntent.DismissError)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
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
                        onQuickCompleteGoal = {
                            viewModel.onIntent(FocusIntent.QuickCompleteGoal(it))
                        },
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

            CelebrationOverlay(
                visible = state.showCelebration,
                message = stringResource(Res.string.focus_completed_title),
                emoji = "\uD83C\uDF1F",
                onDismissed = { viewModel.onIntent(FocusIntent.DismissCelebration) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IdleContent(
    state: FocusState,
    onSelectDuration: (FocusDuration) -> Unit,
    onStart: () -> Unit,
    onQuickCompleteGoal: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.focus_ready),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(8.dp))

        FocusVisual(
            phase = FocusPhase.Idle,
            modifier = Modifier.size(120.dp),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(Res.string.focus_select_duration),
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
                text = stringResource(Res.string.focus_start),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        if (state.activeGoals.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            ActiveGoalsWidget(
                goals = state.activeGoals,
                onQuickComplete = onQuickCompleteGoal,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
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
            text = stringResource(Res.string.focus_in_progress),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(16.dp))

        FocusVisual(
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
            text = stringResource(Res.string.focus_time_remaining),
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
            Text(stringResource(Res.string.focus_cancel))
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
            FocusVisual(
                phase = state.phase,
                modifier = Modifier.size(120.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isCompleted) stringResource(Res.string.focus_success) else stringResource(Res.string.focus_failed),
            style = MaterialTheme.typography.titleMedium,
            color = if (isCompleted) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error,
        )

        Spacer(modifier = Modifier.height(24.dp))

        ResultCard(
            title = if (isCompleted) stringResource(Res.string.focus_completed_title) else stringResource(Res.string.focus_interrupted_title),
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
                text = stringResource(Res.string.focus_continue),
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
    val timerContentDescription = stringResource(Res.string.cd_timer)

    Box(
        modifier = modifier.semantics { contentDescription = timerContentDescription },
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
private fun FocusVisual(
    phase: FocusPhase,
    modifier: Modifier = Modifier,
) {
    val emoji = when (phase) {
        FocusPhase.Idle -> "\uD83D\uDD25"        // fire — ready
        FocusPhase.Focusing -> "\u26A1"           // lightning — in progress
        FocusPhase.Completed -> "\uD83C\uDF1F"    // glowing star — success
        FocusPhase.Interrupted -> "\uD83D\uDCA8"  // dash — interrupted
    }
    val focusStatusContentDescription = stringResource(Res.string.cd_focus_status)

    Box(
        modifier = modifier.semantics { contentDescription = focusStatusContentDescription },
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
            Text("${duration.minutes} ${stringResource(Res.string.focus_minutes_suffix)}")
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
            text = "\u2728",  // sparkles
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${stringResource(Res.string.focus_balance)}: $balance",
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
                    label = stringResource(Res.string.focus_earned_xp),
                    value = "+$earnedXp",
                    icon = "\u2B50",  // star
                )
                RewardItem(
                    label = stringResource(Res.string.focus_earned_coins),
                    value = "+$earnedCoins",
                    icon = "\u2728",  // sparkles
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

@Composable
private fun ActiveGoalsWidget(
    goals: List<WillpowerGoal>,
    onQuickComplete: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(Res.string.focus_active_goals),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            goals.forEach { goal ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "\u2B50 ${goal.xpReward.value}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                            Text(
                                text = "\u2728 ${goal.coinReward.amount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                    IconButton(onClick = { onQuickComplete(goal.id) }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(Res.string.goals_complete),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
        }
    }
}
