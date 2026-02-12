package com.focusclinic.domain.usecase

import com.focusclinic.domain.model.DomainError
import com.focusclinic.domain.model.DomainResult
import com.focusclinic.domain.model.FocusSession
import com.focusclinic.domain.repository.FocusSessionRepository
import com.focusclinic.domain.valueobject.FocusDuration
import kotlinx.coroutines.flow.first

class StartFocusSessionUseCase(
    private val sessionRepository: FocusSessionRepository,
    private val idGenerator: () -> String,
    private val clock: () -> Long,
) {
    suspend operator fun invoke(duration: FocusDuration): DomainResult<FocusSession> {
        val active = sessionRepository.observeActiveSession().first()

        if (active != null) {
            return DomainResult.Failure(DomainError.FocusError.SessionAlreadyActive)
        }

        val session = FocusSession.create(
            id = idGenerator(),
            startTime = clock(),
            plannedDuration = duration,
        )

        sessionRepository.save(session)
        return DomainResult.Success(session)
    }
}
