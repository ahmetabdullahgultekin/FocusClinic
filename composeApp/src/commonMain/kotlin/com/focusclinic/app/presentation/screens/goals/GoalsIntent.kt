package com.focusclinic.app.presentation.screens.goals

import com.focusclinic.domain.model.RecurrenceType
import com.focusclinic.domain.model.WillpowerGoal

sealed interface GoalsIntent {
    data class CreateGoal(
        val title: String,
        val description: String,
        val coinReward: Long,
        val xpReward: Long,
        val recurrenceType: RecurrenceType = RecurrenceType.None,
        val category: String = "",
    ) : GoalsIntent

    data class UpdateGoal(
        val goalId: String,
        val title: String,
        val description: String,
        val coinReward: Long,
        val xpReward: Long,
        val recurrenceType: RecurrenceType = RecurrenceType.None,
        val category: String = "",
    ) : GoalsIntent

    data class CompleteGoal(val goalId: String, val note: String = "") : GoalsIntent
    data class DeactivateGoal(val goalId: String) : GoalsIntent
    data class StartEditing(val goal: WillpowerGoal) : GoalsIntent
    data class ShowCompleteDialog(val goalId: String) : GoalsIntent
    data class SelectDay(val day: Int) : GoalsIntent
    data class SelectCategory(val category: String?) : GoalsIntent
    data object ShowCreateDialog : GoalsIntent
    data object DismissDialog : GoalsIntent
    data object DismissCompleteDialog : GoalsIntent
    data object DismissCelebration : GoalsIntent
    data object DismissDayDetail : GoalsIntent
    data object PreviousMonth : GoalsIntent
    data object NextMonth : GoalsIntent
    data object DismissError : GoalsIntent
    data object DismissSuccess : GoalsIntent
}
