package glowie.glowie_app.syncClock.domain

import kotlin.time.DurationUnit
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class SyncClock (
    private val timeSource: TimeSource = TimeSource.Monotonic,
    private val startMark: TimeMark = timeSource.markNow()
) {
    fun getTime(): Int = startMark.elapsedNow().toInt(DurationUnit.MILLISECONDS)
}
