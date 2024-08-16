package viewmodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InspectionItems(
@SerialName("end")val end:String,
    @SerialName("message") val message: String,
    @SerialName("status") val status: String
)