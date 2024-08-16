package viewmodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TruckPrecheck(
@SerialName("trailer_precheck") val trailer_precheck:String,
    @SerialName("message") val message: String,
    @SerialName("status") val status: String
)