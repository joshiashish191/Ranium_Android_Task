package net.softglobe.raniumandroidtask.data

data class NeoFeed(
    val element_count: Int,
    val near_earth_objects: HashMap<String, List<DataForADay>>

)