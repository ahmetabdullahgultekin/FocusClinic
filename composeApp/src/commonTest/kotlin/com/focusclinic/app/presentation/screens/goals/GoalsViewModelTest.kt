package com.focusclinic.app.presentation.screens.goals

import com.focusclinic.app.presentation.FakeTransactionRepository
import com.focusclinic.app.presentation.FakeUserProfileRepository
import com.focusclinic.app.presentation.FakeWillpowerGoalRepository
import com.focusclinic.domain.usecase.CompleteWillpowerGoalUseCase
import com.focusclinic.domain.usecase.CreateWillpowerGoalUseCase
import com.focusclinic.domain.usecase.DeactivateWillpowerGoalUseCase
import com.focusclinic.domain.usecase.UpdateWillpowerGoalUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GoalsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val goalRepo = FakeWillpowerGoalRepository()
    private val transactionRepo = FakeTransactionRepository()
    private val userProfileRepo = FakeUserProfileRepository()
    private var idCounter = 0

    private lateinit var viewModel: GoalsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        idCounter = 0
        viewModel = GoalsViewModel(
            createWillpowerGoal = CreateWillpowerGoalUseCase(
                goalRepository = goalRepo,
                idGenerator = { "goal-${++idCounter}" },
                clock = { 1000L },
            ),
            completeWillpowerGoal = CompleteWillpowerGoalUseCase(
                goalRepository = goalRepo,
                transactionRepository = transactionRepo,
                userProfileRepository = userProfileRepo,
                idGenerator = { "comp-${++idCounter}" },
                clock = { 2000L },
            ),
            updateWillpowerGoal = UpdateWillpowerGoalUseCase(
                goalRepository = goalRepo,
                clock = { 3000L },
            ),
            deactivateWillpowerGoal = DeactivateWillpowerGoalUseCase(
                goalRepository = goalRepo,
            ),
            goalRepository = goalRepo,
            scope = testScope,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_ShouldBeEmpty() = testScope.runTest {
        advanceUntilIdle()
        assertTrue(viewModel.state.value.goals.isEmpty())
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun createGoal_ShouldAddToList() = testScope.runTest {
        advanceUntilIdle()
        viewModel.onIntent(GoalsIntent.CreateGoal("Be patient", "With siblings", 10, 20))
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.goals.size)
        assertEquals("Be patient", viewModel.state.value.goals[0].title)
    }

    @Test
    fun completeGoal_ShouldRecordCompletion() = testScope.runTest {
        advanceUntilIdle()
        viewModel.onIntent(GoalsIntent.CreateGoal("Test", "Desc", 10, 20))
        advanceUntilIdle()

        val goalId = viewModel.state.value.goals[0].id
        viewModel.onIntent(GoalsIntent.CompleteGoal(goalId))
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.recentCompletions.size)
    }

    @Test
    fun deactivateGoal_ShouldRemoveFromList() = testScope.runTest {
        advanceUntilIdle()
        viewModel.onIntent(GoalsIntent.CreateGoal("Test", "Desc", 10, 20))
        advanceUntilIdle()

        val goalId = viewModel.state.value.goals[0].id
        viewModel.onIntent(GoalsIntent.DeactivateGoal(goalId))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.goals.isEmpty())
    }

    @Test
    fun showCreateDialog_ShouldUpdateState() = testScope.runTest {
        advanceUntilIdle()
        viewModel.onIntent(GoalsIntent.ShowCreateDialog)
        assertTrue(viewModel.state.value.showCreateDialog)
    }

    @Test
    fun dismissDialog_ShouldClearDialogState() = testScope.runTest {
        advanceUntilIdle()
        viewModel.onIntent(GoalsIntent.ShowCreateDialog)
        viewModel.onIntent(GoalsIntent.DismissDialog)
        assertFalse(viewModel.state.value.showCreateDialog)
        assertNull(viewModel.state.value.editingGoal)
    }

    @Test
    fun dismissError_ShouldClearError() = testScope.runTest {
        advanceUntilIdle()
        viewModel.onIntent(GoalsIntent.DismissError)
        assertNull(viewModel.state.value.errorMessage)
    }
}
