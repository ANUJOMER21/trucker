package viewmodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrecheckData(

   @SerialName("message") val message: String,
    @SerialName("status") val status: String
)