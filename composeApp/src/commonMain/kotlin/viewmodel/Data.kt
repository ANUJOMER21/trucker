package viewmodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
  @SerialName("date")  val date: String,
   @SerialName("driver_id") val driver_id: String,
    @SerialName("image_path")val image_path: String,
   @SerialName("meter_reading") val meter_reading: String
)