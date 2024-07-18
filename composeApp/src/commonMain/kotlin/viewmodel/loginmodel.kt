package viewmodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class loginmodel(
  val driver: Driver,
    val message: String,
    val status: String
) {


}