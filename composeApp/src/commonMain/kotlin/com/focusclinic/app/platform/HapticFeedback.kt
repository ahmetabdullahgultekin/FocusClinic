package com.focusclinic.app.platform

/**
 * Interface for platform-specific haptic feedback.
 * Extracted as interface (not just expect class) for testability.
 */
interface HapticFeedback {
    fun light()
    fun medium()
    fun heavy()
    fun success()
    fun error()
}

expect class HapticFeedbackManager : HapticFeedback
