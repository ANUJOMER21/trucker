package viewmodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrecheckData(
@SerialName("truck_precheck") val truck_precheck:String,
   @SerialName("message") val message: String,
    @SerialName("status") val status: String
)