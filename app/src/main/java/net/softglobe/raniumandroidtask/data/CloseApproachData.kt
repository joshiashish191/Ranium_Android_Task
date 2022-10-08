package net.softglobe.raniumandroidtask.data

data class CloseApproachData(
    val close_approach_date: String,
    val close_approach_date_full: String,
    val epoch_date_close_approach: Long,
    val miss_distance: MissDistanceX,
    val relative_velocity: RelativeVelocityX
)