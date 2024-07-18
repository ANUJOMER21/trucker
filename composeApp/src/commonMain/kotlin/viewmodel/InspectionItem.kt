package viewmodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Inspection(
   @SerialName("item_name") val item_name: String,
   @SerialName("status") val status: String
)