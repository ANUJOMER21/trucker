package viewmodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class profilemodel(
  @SerialName("data")  val `data`: DataX,
  @SerialName("status")  val status: String
)