package viewmodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DATAXX(
    @SerialName("Data")  val Data: DataXXX,
   @SerialName("Date")  val Date: String
)