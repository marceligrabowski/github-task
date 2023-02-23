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
        // should count request before trying to get data?
        saveUserRequestInformation(login)

        return githubApi.getUser(login)
            .toUser()
    }

    private fun saveUserRequestInformation(login: String) {
        val userRequestEntity =
            userRequestRepository.findById(login).orElse(UserRequest(login, 0))
                ?: UserRequest(login, 0)
        userRequestRepository.save(
            userRequestEntity.copy(requests = userRequestEntity.requests + 1)
        )
    }
}