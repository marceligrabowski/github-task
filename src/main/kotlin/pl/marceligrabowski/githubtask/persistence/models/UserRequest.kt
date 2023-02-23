package pl.marceligrabowski.githubtask.persistence.models

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class UserRequest(@Id val login: String, val requestCount: Int = 0) 