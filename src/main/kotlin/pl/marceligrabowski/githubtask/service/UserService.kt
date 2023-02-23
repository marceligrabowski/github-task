package pl.marceligrabowski.githubtask.service

import org.springframework.stereotype.Service
import pl.marceligrabowski.githubtask.external.api.GithubApi
import pl.marceligrabowski.githubtask.models.User
import pl.marceligrabowski.githubtask.models.toUser
import pl.marceligrabowski.githubtask.persistence.models.UserRequest
import pl.marceligrabowski.githubtask.persistence.repository.UserRequestRepository

@Service
class UserService(
    private val githubApi: GithubApi,
    private val userRequestRepository: UserRequestRepository
) {

    fun getUser(login: String): User {
        // todo: should count request before trying to get data + what if db is not working?
        incrementUserRequestCount(login)

        // todo: add cache? e.g. caffeine
        return githubApi.getUser(login)
            .toUser()
    }

    private fun incrementUserRequestCount(login: String) {
        val userRequestEntity =
            userRequestRepository.findById(login)
                .orElse(UserRequest(login, 0))
                .let {
                    it.copy(requestCount = it.requestCount + 1)
                }

        userRequestRepository.save(userRequestEntity)
    }
}