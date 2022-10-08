package net.softglobe.raniumandroidtask.data

data class DataForADay(
    val close_approach_data: List<CloseApproachData>,
    val id: String,
    val estimated_diameter : EstimatedDiameter
)