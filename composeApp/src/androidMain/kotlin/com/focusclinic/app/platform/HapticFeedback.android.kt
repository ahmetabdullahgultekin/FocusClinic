package com.focusclinic.app.platform

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

actual class HapticFeedbackManager(private val context: Context) : HapticFeedback {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    override fun light() = vibrate(30L, VibrationEffect.EFFECT_TICK)

    override fun medium() = vibrate(50L, VibrationEffect.EFFECT_CLICK)

    override fun heavy() = vibrate(80L, VibrationEffect.EFFECT_HEAVY_CLICK)

    override fun success() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator?.vibrate(
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK),
            )
        } else {
            vibrate(100L)
        }
    }

    override fun error() = vibrate(200L)

    private fun vibrate(durationMs: Long, effectId: Int? = null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && effectId != null) {
            vibrator?.vibrate(VibrationEffect.createPredefined(effectId))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE),
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(durationMs)
        }
    }
}
