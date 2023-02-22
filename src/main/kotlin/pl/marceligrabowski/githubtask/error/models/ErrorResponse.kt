package pl.marceligrabowski.githubtask.error.models

import java.time.Instant

data class ErrorResponse(
    val code: Int,
    val status: String,
    val message: String?,
    val details: String,
    val timestamp: Instant = Instant.now(),
    ) 