package com.rarilabs.rarime.util

import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

class RandomUtil private constructor() {
    companion object {
        private const val RANDOM_DELAY_MEAN = 1_000.0
        private const val RANDOM_DELAY_STD_DEV = 200.0
        private const val MIN_DELAY = 500L
        private const val STEPS = 25

        private fun randomNormal(mu: Double = RANDOM_DELAY_MEAN, sigma: Double = RANDOM_DELAY_STD_DEV): Double {
            val u1 = Random.nextDouble(0.0, 1.0)
            val u2 = Random.nextDouble(0.0, 1.0)
            val z0 = sqrt(-2.0 * ln(u1)) * cos(2.0 * Math.PI * u2)
            return mu + sigma * z0
        }

        suspend fun updateProgressWithRandomDelay(
            steps: Int = STEPS,
            durationInSeconds: Int,
            updateProgress: (Float) -> Unit
        ) {
            val stepDuration = durationInSeconds.toFloat() / steps
            for (i in 0 until steps) {
                val randomDelay = randomNormal().toLong()
                val delayTime = (stepDuration * 1_000).toLong() + randomDelay
                delay(delayTime.coerceAtLeast(MIN_DELAY))
                val progress = i / steps.toFloat()
                updateProgress(progress)
            }
        }
    }
}
