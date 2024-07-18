package viewmodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class endtripmodel(
    @SerialName("data")val data: Data,
   @SerialName("status") val status: String
)