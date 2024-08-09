package viewmodel

import kotlinx.serialization.Serializable

@Serializable
data class DataXXX(
    val inspection_items: String,
    val precheck_data: String,
    val truck_precheck: String
)