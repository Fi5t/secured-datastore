package com.redmadrobot.securedatastore

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("name") val name: String? = null,
    @SerialName("password") val password: String? = null,
)
