package viewmodel

import kotlinx.serialization.Serializable

@Serializable
data class HistoryModel(
    val history: History,
    val status: String
)