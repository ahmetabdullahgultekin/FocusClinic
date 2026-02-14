package com.focusclinic.app.presentation.screens.goals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.focusclinic.app.presentation.Strings
import com.focusclinic.app.presentation.components.CalendarHeatmap
import com.focusclinic.domain.model.WillpowerGoal

@Composable
fun GoalsScreen(viewModel: GoalsViewModel) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onIntent(GoalsIntent.DismissError)
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(Strings.GOALS_COMPLETED_SUCCESS)
            viewModel.onIntent(GoalsIntent.DismissSuccess)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onIntent(GoalsIntent.ShowCreateDialog) },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = Strings.GOALS_ADD,
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = Strings.GOALS_TITLE,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp),
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                ),
            ) {
                CalendarHeatmap(
                    year = state.calendarYear,
                    month = state.calendarMonth,
                    completionCounts = state.calendarCompletionCounts,
                    onPreviousMonth = { viewModel.onIntent(GoalsIntent.PreviousMonth) },
                    onNextMonth = { viewModel.onIntent(GoalsIntent.NextMonth) },
                    onDayClick = { },
                    modifier = Modifier.padding(8.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(
                visible = state.isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            AnimatedVisibility(
                visible = !state.isLoading && state.goals.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                EmptyGoalsContent()
            }

            AnimatedVisibility(
                visible = !state.isLoading && state.goals.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.goals, key = { it.id }) { goal ->
                        GoalCard(
                            goal = goal,
                            onComplete = { viewModel.onIntent(GoalsIntent.CompleteGoal(goal.id)) },
                            onEdit = { viewModel.onIntent(GoalsIntent.StartEditing(goal)) },
                            onDelete = { viewModel.onIntent(GoalsIntent.DeactivateGoal(goal.id)) },
                        )
                    }
                }
            }
        }
    }

    if (state.showCreateDialog) {
        GoalFormDialog(
            title = Strings.GOALS_CREATE_TITLE,
            onDismiss = { viewModel.onIntent(GoalsIntent.DismissDialog) },
            onConfirm = { title, desc, coins, xp ->
                viewModel.onIntent(GoalsIntent.CreateGoal(title, desc, coins, xp))
            },
            isProcessing = state.isProcessing,
        )
    }

    state.editingGoal?.let { goal ->
        GoalFormDialog(
            title = Strings.GOALS_EDIT_TITLE,
            initialTitle = goal.title,
            initialDescription = goal.description,
            initialCoinReward = goal.coinReward.amount.toString(),
            initialXpReward = goal.xpReward.value.toString(),
            onDismiss = { viewModel.onIntent(GoalsIntent.DismissDialog) },
            onConfirm = { title, desc, coins, xp ->
                viewModel.onIntent(GoalsIntent.UpdateGoal(goal.id, title, desc, coins, xp))
            },
            isProcessing = state.isProcessing,
        )
    }
}

@Composable
private fun EmptyGoalsContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = Strings.GOALS_EMPTY_ICON,
                style = MaterialTheme.typography.displayLarge,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = Strings.GOALS_EMPTY,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun GoalCard(
    goal: WillpowerGoal,
    onComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    if (goal.description.isNotBlank()) {
                        Text(
                            text = goal.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Row {
                    IconButton(onClick = onComplete) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = Strings.GOALS_COMPLETE,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = Strings.GOALS_EDIT,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = Strings.GOALS_DELETE,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "${Strings.GOALS_COIN_REWARD}: ${goal.coinReward.amount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = "${Strings.GOALS_XP_REWARD}: ${goal.xpReward.value}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}

@Composable
private fun GoalFormDialog(
    title: String,
    initialTitle: String = "",
    initialDescription: String = "",
    initialCoinReward: String = "10",
    initialXpReward: String = "20",
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long, Long) -> Unit,
    isProcessing: Boolean,
) {
    var goalTitle by remember { mutableStateOf(initialTitle) }
    var goalDescription by remember { mutableStateOf(initialDescription) }
    var coinReward by remember { mutableStateOf(initialCoinReward) }
    var xpReward by remember { mutableStateOf(initialXpReward) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = goalTitle,
                    onValueChange = { goalTitle = it },
                    label = { Text(Strings.GOALS_FIELD_TITLE) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = goalDescription,
                    onValueChange = { goalDescription = it },
                    label = { Text(Strings.GOALS_FIELD_DESCRIPTION) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = coinReward,
                        onValueChange = { coinReward = it },
                        label = { Text(Strings.GOALS_FIELD_COINS) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = xpReward,
                        onValueChange = { xpReward = it },
                        label = { Text(Strings.GOALS_FIELD_XP) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val coins = coinReward.toLongOrNull() ?: 0
                    val xp = xpReward.toLongOrNull() ?: 0
                    onConfirm(goalTitle, goalDescription, coins, xp)
                },
                enabled = !isProcessing && goalTitle.isNotBlank(),
            ) {
                Text(Strings.GOALS_SAVE)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.GOALS_CANCEL)
            }
        },
    )
}
