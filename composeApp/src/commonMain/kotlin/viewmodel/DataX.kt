package viewmodel

import kotlinx.serialization.Serializable

@Serializable
data class DataX(
    val Dob: String,
    val Email: String,
    val EmergencyContact: String,
    val ID: String,
    val LicenseNumber: String,
    val Mobile: String,
    val Name: String,
    val Password: String
)