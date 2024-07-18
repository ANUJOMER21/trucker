package viewmodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class checkdata(
   @SerialName("precheck_data") val precheck_data: PrecheckData,
   @SerialName("truck_precheck") val truck_precheck: TruckPrecheck,
   @SerialName("inspection_items") val inspectionItems: InspectionItems
)