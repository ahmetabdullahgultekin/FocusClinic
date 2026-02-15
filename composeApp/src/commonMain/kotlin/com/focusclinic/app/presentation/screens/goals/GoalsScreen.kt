package com.focusclinic.app.presentation.screens.goals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import focusclinic.composeapp.generated.resources.Res
import focusclinic.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import com.focusclinic.app.presentation.components.CalendarHeatmap
import com.focusclinic.app.presentation.components.CelebrationOverlay
import com.focusclinic.domain.model.GoalCategory
import com.focusclinic.domain.model.RecurrenceType
import com.focusclinic.domain.model.WillpowerGoal
import com.focusclinic.domain.rule.StreakRules

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

    val completedSuccessText = stringResource(Res.string.goals_completed_success)
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(completedSuccessText)
            viewModel.onIntent(GoalsIntent.DismissSuccess)
        }
    }

    val filteredGoals = if (state.selectedCategory != null) {
        state.goals.filter { it.category == state.selectedCategory }
    } else {
        state.goals
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
                    contentDescription = stringResource(Res.string.goals_add),
                )
            }
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    text = stringResource(Res.string.goals_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp),
                )

                StreakCard(
                    currentStreak = state.currentStreak,
                    bestStreak = state.bestStreak,
                    streakMultiplier = state.streakMultiplier,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                        selectedDay = state.selectedDay,
                        onPreviousMonth = { viewModel.onIntent(GoalsIntent.PreviousMonth) },
                        onNextMonth = { viewModel.onIntent(GoalsIntent.NextMonth) },
                        onDayClick = { viewModel.onIntent(GoalsIntent.SelectDay(it)) },
                        modifier = Modifier.padding(8.dp),
                    )
                }

                AnimatedVisibility(
                    visible = state.selectedDay != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    DayDetailSection(
                        day = state.selectedDay ?: 0,
                        completions = state.selectedDayCompletions,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                if (state.availableCategories.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CategoryFilterRow(
                        categories = state.availableCategories,
                        selectedCategory = state.selectedCategory,
                        onCategorySelected = { viewModel.onIntent(GoalsIntent.SelectCategory(it)) },
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
                    visible = !state.isLoading && filteredGoals.isEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    EmptyGoalsContent()
                }

                AnimatedVisibility(
                    visible = !state.isLoading && filteredGoals.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(filteredGoals, key = { it.id }) { goal ->
                            val isCompletable = goal.id in state.completableGoalIds
                            GoalCard(
                                goal = goal,
                                isCompletable = isCompletable,
                                onComplete = {
                                    viewModel.onIntent(GoalsIntent.ShowCompleteDialog(goal.id))
                                },
                                onEdit = { viewModel.onIntent(GoalsIntent.StartEditing(goal)) },
                                onDelete = {
                                    viewModel.onIntent(GoalsIntent.DeactivateGoal(goal.id))
                                },
                            )
                        }
                    }
                }
            }

            CelebrationOverlay(
                visible = state.showCelebration,
                message = stringResource(Res.string.goals_completed_success),
                emoji = "\uD83C\uDF89",
                onDismissed = { viewModel.onIntent(GoalsIntent.DismissCelebration) },
            )
        }
    }

    if (state.showCreateDialog) {
        GoalFormDialog(
            title = stringResource(Res.string.goals_create_title),
            onDismiss = { viewModel.onIntent(GoalsIntent.DismissDialog) },
            onConfirm = { title, desc, coins, xp, recurrence, category ->
                viewModel.onIntent(
                    GoalsIntent.CreateGoal(title, desc, coins, xp, recurrence, category),
                )
            },
            isProcessing = state.isProcessing,
        )
    }

    state.editingGoal?.let { goal ->
        GoalFormDialog(
            title = stringResource(Res.string.goals_edit_title),
            initialTitle = goal.title,
            initialDescription = goal.description,
            initialCoinReward = goal.coinReward.amount.toString(),
            initialXpReward = goal.xpReward.value.toString(),
            initialRecurrenceType = goal.recurrenceType,
            initialCategory = goal.category,
            onDismiss = { viewModel.onIntent(GoalsIntent.DismissDialog) },
            onConfirm = { title, desc, coins, xp, recurrence, category ->
                viewModel.onIntent(
                    GoalsIntent.UpdateGoal(goal.id, title, desc, coins, xp, recurrence, category),
                )
            },
            isProcessing = state.isProcessing,
        )
    }

    state.completingGoalId?.let { goalId ->
        CompletionNoteDialog(
            onDismiss = { viewModel.onIntent(GoalsIntent.DismissCompleteDialog) },
            onComplete = { note ->
                viewModel.onIntent(GoalsIntent.CompleteGoal(goalId, note))
            },
        )
    }
}

@Composable
private fun StreakCard(
    currentStreak: Int,
    bestStreak: Int,
    streakMultiplier: Double,
    modifier: Modifier = Modifier,
) {
    val streakDesc = stringResource(Res.string.streak_current)
    Card(
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = "$streakDesc: $currentStreak"
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "\uD83D\uDD25",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "$currentStreak ${stringResource(Res.string.streak_days)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = stringResource(Res.string.streak_current),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "\uD83C\uDFC6",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "$bestStreak ${stringResource(Res.string.streak_days)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = stringResource(Res.string.streak_best),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                )
            }

            if (streakMultiplier > StreakRules.BASE_MULTIPLIER) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "\u26A1",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "${streakMultiplier}x",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = stringResource(Res.string.streak_bonus),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text(stringResource(Res.string.category_all)) },
        )
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = {
                    onCategorySelected(if (selectedCategory == category) null else category)
                },
                label = { Text(categoryDisplayName(category)) },
            )
        }
    }
}

@Composable
private fun categoryDisplayName(category: String): String {
    return when (category) {
        GoalCategory.HEALTH -> stringResource(Res.string.category_health)
        GoalCategory.PRODUCTIVITY -> stringResource(Res.string.category_productivity)
        GoalCategory.LEARNING -> stringResource(Res.string.category_learning)
        GoalCategory.FITNESS -> stringResource(Res.string.category_fitness)
        GoalCategory.HABITS -> stringResource(Res.string.category_habits)
        GoalCategory.OTHER -> stringResource(Res.string.category_other)
        else -> category
    }
}

@Composable
private fun recurrenceDisplayName(recurrenceType: RecurrenceType): String {
    return when (recurrenceType) {
        RecurrenceType.None -> stringResource(Res.string.recurrence_none)
        RecurrenceType.Daily -> stringResource(Res.string.recurrence_daily)
        RecurrenceType.Weekly -> stringResource(Res.string.recurrence_weekly)
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
                text = "\uD83C\uDFAF",
                style = MaterialTheme.typography.displayLarge,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.goals_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun GoalCard(
    goal: WillpowerGoal,
    isCompletable: Boolean,
    onComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val cardAlpha = if (isCompletable) 1f else 0.6f

    Card(
        modifier = Modifier.fillMaxWidth().alpha(cardAlpha),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        if (goal.recurrenceType != RecurrenceType.None) {
                            RecurrenceBadge(goal.recurrenceType)
                        }
                    }
                    if (goal.description.isNotBlank()) {
                        Text(
                            text = goal.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Row {
                    IconButton(
                        onClick = onComplete,
                        enabled = isCompletable,
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = stringResource(Res.string.goals_complete),
                            tint = if (isCompletable) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            },
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = stringResource(Res.string.goals_edit),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = stringResource(Res.string.goals_delete),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${stringResource(Res.string.goals_coin_reward)}: ${goal.coinReward.amount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = "${stringResource(Res.string.goals_xp_reward)}: ${goal.xpReward.value}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                if (goal.category.isNotBlank()) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = categoryDisplayName(goal.category),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                    )
                }
            }
            if (!isCompletable && goal.recurrenceType != RecurrenceType.None) {
                Spacer(modifier = Modifier.height(4.dp))
                val completedText = when (goal.recurrenceType) {
                    RecurrenceType.Daily -> stringResource(Res.string.goals_completed_today)
                    RecurrenceType.Weekly -> stringResource(Res.string.goals_completed_this_week)
                    else -> ""
                }
                Text(
                    text = "\u2705 $completedText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun RecurrenceBadge(recurrenceType: RecurrenceType) {
    val emoji = when (recurrenceType) {
        RecurrenceType.Daily -> "\uD83D\uDCC5"
        RecurrenceType.Weekly -> "\uD83D\uDD04"
        RecurrenceType.None -> return
    }
    val label = recurrenceDisplayName(recurrenceType)
    Text(
        text = "$emoji $label",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalFormDialog(
    title: String,
    initialTitle: String = "",
    initialDescription: String = "",
    initialCoinReward: String = "10",
    initialXpReward: String = "20",
    initialRecurrenceType: RecurrenceType = RecurrenceType.None,
    initialCategory: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long, Long, RecurrenceType, String) -> Unit,
    isProcessing: Boolean,
) {
    var goalTitle by remember { mutableStateOf(initialTitle) }
    var goalDescription by remember { mutableStateOf(initialDescription) }
    var coinReward by remember { mutableStateOf(initialCoinReward) }
    var xpReward by remember { mutableStateOf(initialXpReward) }
    var selectedRecurrence by remember { mutableStateOf(initialRecurrenceType) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = goalTitle,
                    onValueChange = { goalTitle = it },
                    label = { Text(stringResource(Res.string.goals_field_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = goalDescription,
                    onValueChange = { goalDescription = it },
                    label = { Text(stringResource(Res.string.goals_field_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = coinReward,
                        onValueChange = { coinReward = it },
                        label = { Text(stringResource(Res.string.goals_field_coins)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = xpReward,
                        onValueChange = { xpReward = it },
                        label = { Text(stringResource(Res.string.goals_field_xp)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                }

                Text(
                    text = stringResource(Res.string.goals_field_recurrence),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RecurrenceType.None.let { type ->
                        FilterChip(
                            selected = selectedRecurrence == type,
                            onClick = { selectedRecurrence = type },
                            label = { Text(recurrenceDisplayName(type)) },
                        )
                    }
                    RecurrenceType.Daily.let { type ->
                        FilterChip(
                            selected = selectedRecurrence == type,
                            onClick = { selectedRecurrence = type },
                            label = { Text(recurrenceDisplayName(type)) },
                        )
                    }
                    RecurrenceType.Weekly.let { type ->
                        FilterChip(
                            selected = selectedRecurrence == type,
                            onClick = { selectedRecurrence = type },
                            label = { Text(recurrenceDisplayName(type)) },
                        )
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = it },
                ) {
                    OutlinedTextField(
                        value = if (selectedCategory.isBlank()) {
                            stringResource(Res.string.category_all)
                        } else {
                            categoryDisplayName(selectedCategory)
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(Res.string.goals_field_category)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.category_all)) },
                            onClick = {
                                selectedCategory = GoalCategory.NONE
                                categoryDropdownExpanded = false
                            },
                        )
                        GoalCategory.PREDEFINED.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(categoryDisplayName(category)) },
                                onClick = {
                                    selectedCategory = category
                                    categoryDropdownExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val coins = coinReward.toLongOrNull() ?: 0
                    val xp = xpReward.toLongOrNull() ?: 0
                    onConfirm(goalTitle, goalDescription, coins, xp, selectedRecurrence, selectedCategory)
                },
                enabled = !isProcessing && goalTitle.isNotBlank(),
            ) {
                Text(stringResource(Res.string.goals_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.goals_cancel))
            }
        },
    )
}

@Composable
private fun CompletionNoteDialog(
    onDismiss: () -> Unit,
    onComplete: (String) -> Unit,
) {
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.goals_complete)) },
        text = {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(Res.string.goals_completion_note)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
            )
        },
        confirmButton = {
            Button(onClick = { onComplete(note.trim()) }) {
                Text(stringResource(Res.string.goals_complete))
            }
        },
        dismissButton = {
            TextButton(onClick = { onComplete("") }) {
                Text(stringResource(Res.string.goals_skip))
            }
        },
    )
}

@Composable
private fun DayDetailSection(
    day: Int,
    completions: List<GoalCompletionDetail>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (completions.isEmpty()) {
                Text(
                    text = stringResource(Res.string.calendar_no_completions),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                completions.forEachIndexed { index, detail ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = detail.goalTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                            )
                            if (detail.completion.note.isNotBlank()) {
                                Text(
                                    text = detail.completion.note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "\u2B50 +${detail.completion.earnedXp.value}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                            Text(
                                text = "\u2728 +${detail.completion.earnedCoins.amount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                    if (index < completions.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        )
                    }
                }
            }
        }
    }
}
