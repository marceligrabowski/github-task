package pl.marceligrabowski.githubtask.external.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class GithubUser(
    val id: Long,
    val login: String,
    val name: String,
    val type: String,
    @JsonProperty("avatar_url") val avatarUrl: String,
    @JsonProperty("created_at") val createdAt: OffsetDateTime,
    val followers: Int,
    @JsonProperty("public_repos") val publicRepos: Int
)
