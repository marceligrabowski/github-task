package pl.marceligrabowski.githubtask.models

import pl.marceligrabowski.githubtask.external.models.GithubUser
import java.time.OffsetDateTime

data class User(
    val id: Long,
    val login: String,
    val name: String,
    val type: String,
    val avatarUrl: String,
    val createdAt: OffsetDateTime,
    val calculations: Double
)

fun GithubUser.toUser() =
    User(
        this.id,
        this.login,
        this.name,
        this.type,
        this.avatarUrl,
        this.createdAt,
        this.calculate()
    )

// TODO: What if followers are 0 -> currently throw random exception
fun GithubUser.calculate(): Double {
    if (this.followers == 0) throw Exception()
    
    return 6.0 / this.followers * (2.0 + this.publicRepos)
}