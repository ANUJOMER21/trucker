package viewmodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class trailer_model(
   @SerialName("driver_id") val driver_id: String,
    @SerialName("inspection_items")val inspection_items: List<Inspection>
)