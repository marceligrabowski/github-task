package pl.marceligrabowski.githubtask.service

import org.springframework.stereotype.Service
import pl.marceligrabowski.githubtask.external.api.GithubApi
import pl.marceligrabowski.githubtask.models.User
import pl.marceligrabowski.githubtask.models.toUser

@Service
class UserService(private val githubApi: GithubApi) {
    fun getUser(login: String): User {
        return githubApi.getUser(login)
            .toUser()
    }
}