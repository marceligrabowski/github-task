package pl.marceligrabowski.githubtask.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.marceligrabowski.githubtask.external.api.GithubApi
import pl.marceligrabowski.githubtask.external.models.GithubUser
import pl.marceligrabowski.githubtask.models.User
import pl.marceligrabowski.githubtask.models.toUser
import pl.marceligrabowski.githubtask.persistence.models.UserRequest
import pl.marceligrabowski.githubtask.persistence.repository.UserRequestRepository
import java.time.OffsetDateTime
import java.util.*
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class UserServiceTest {

    companion object {
        private const val LOGIN = "randomLogin"
        private val GITHUB_USER = GithubUser(
            1,
            "",
            "",
            "",
            "",
            OffsetDateTime.now(),
            10,
            10
        )
        private val USER = User(1, "", "", "", "", OffsetDateTime.now(), 4.0)

        @JvmStatic
        val argumentStream: Stream<Arguments> =
            Stream.of(
                Arguments.of(1, null),
                Arguments.of(3, UserRequest(LOGIN, 2))
            )
    }


    @MockK
    lateinit var githubApi: GithubApi

    @MockK
    lateinit var userRequestRepository: UserRequestRepository

    @InjectMockKs
    lateinit var userService: UserService


    @ParameterizedTest(name = "should return user and set request count to {0}")
    @MethodSource("getArgumentStream")
    fun shouldReturnUser_FirstRequest(
        expectedRequestCount: Int,
        existingUserRequestEntity: UserRequest?
    ) {
        val expectedUserRequest = UserRequest(LOGIN, expectedRequestCount)

        val userRequestCapture = slot<UserRequest>()

        every { userRequestRepository.findById(LOGIN) } returns Optional.ofNullable(
            existingUserRequestEntity
        )
        every { userRequestRepository.save(capture(userRequestCapture)) } returns expectedUserRequest

        every { githubApi.getUser(LOGIN) } returns GITHUB_USER
        mockkStatic("pl.marceligrabowski.githubtask.models.UserKt")
        every { GITHUB_USER.toUser() } returns USER

        val result = userService.getUser(LOGIN)

        verify { userRequestRepository.findById(LOGIN) }
        verify { userRequestRepository.save(userRequestCapture.captured) }
        verify { githubApi.getUser(LOGIN) }
        verify { GITHUB_USER.toUser() }


        assertThat(result)
            .isEqualTo(USER)
            .usingRecursiveComparison()
        assertThat(userRequestCapture.captured)
            .isEqualTo(expectedUserRequest)
            .usingRecursiveComparison()
    }
}