package pl.marceligrabowski.githubtask.models

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import pl.marceligrabowski.githubtask.error.exception.CalculationNotPossibleException
import pl.marceligrabowski.githubtask.external.models.GithubUser
import java.time.OffsetDateTime


class UserKtTest {
    companion object {
        private const val ID = 1L
        private const val LOGIN = "user123"
        private const val NAME = "User123"
        private const val TYPE = "User"
        private const val AVATAR_URL = "url12"
        private const val CREATED_AT = "2023-02-19T10:25:34Z"
        private const val FOLLOWERS = 6
        private const val PUBLIC_REPO = 2
    }

    @Test
    fun shouldCalculateCorrectly() {
        val githubUser = GithubUser(
            ID, 
            LOGIN, 
            NAME, 
            TYPE, 
            AVATAR_URL, 
            OffsetDateTime.parse(CREATED_AT),
            FOLLOWERS,
            PUBLIC_REPO
        )

        assertThat(githubUser.calculate()).isEqualTo(4.0)
    }

    @Test
    fun shouldCalculationThrowException() {
        val githubUser = GithubUser(
            ID,
            LOGIN,
            NAME,
            TYPE,
            AVATAR_URL,
            OffsetDateTime.parse(CREATED_AT),
            0,
            PUBLIC_REPO
        )

        assertThatThrownBy { githubUser.calculate() }
            .isInstanceOf(CalculationNotPossibleException::class.java)
    }

    @Test
    fun shouldMapGithubUserToUser() {
        val githubUser = GithubUser(
            1,
            LOGIN,
            NAME,
            TYPE,
            AVATAR_URL,
            OffsetDateTime.parse(CREATED_AT),
            FOLLOWERS,
            PUBLIC_REPO
        )
        val expectedUser = User(
            1,
            LOGIN,
            NAME,
            TYPE,
            AVATAR_URL,
            OffsetDateTime.parse(CREATED_AT),
            4.0
        )
        
        assertThat(githubUser.toUser())
            .isEqualTo(expectedUser)
            .usingRecursiveComparison()
    }
}