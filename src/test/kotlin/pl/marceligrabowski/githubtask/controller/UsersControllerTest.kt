package pl.marceligrabowski.githubtask.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.JsonBody.json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import pl.marceligrabowski.githubtask.error.models.ErrorResponse
import pl.marceligrabowski.githubtask.models.User
import pl.marceligrabowski.githubtask.persistence.models.UserRequest
import pl.marceligrabowski.githubtask.persistence.repository.UserRequestRepository
import java.time.OffsetDateTime
import java.util.stream.Stream

//TODO: Tidy up
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@Testcontainers
class UsersControllerTest {

    @Autowired
    private lateinit var userRequestRepository: UserRequestRepository

    @LocalServerPort
    private val port = 0


    companion object {
        private val mockServerImage: DockerImageName =
            DockerImageName.parse("mockserver/mockserver")
                .withTag("mockserver-" + MockServerClient::class.java.getPackage().implementationVersion)
        val mockServer = MockServerContainer(mockServerImage)
        private val postgreSQLContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("github-task")
            .withUsername("sa")
            .withPassword("sa")

        init {
            mockServer.start()
            postgreSQLContainer.start()
            System.setProperty("github.api.url", mockServer.endpoint)
            System.setProperty("spring.datasource.url", postgreSQLContainer.jdbcUrl)
            System.setProperty("spring.datasource.username", postgreSQLContainer.username)
            System.setProperty("spring.datasource.password", postgreSQLContainer.password)
        }

        @JvmStatic
        fun provideExpectedErrorResponses(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    404,
                    404,
                    "user123",
                    ErrorResponse(
                        404,
                        "NOT_FOUND",
                        "User not found",
                        "uri=/users/user123"
                    )
                ),
                Arguments.of(
                    500,
                    500,
                    "user1234",
                    ErrorResponse(
                        500,
                        "INTERNAL_SERVER_ERROR",
                        "Internal server error",
                        "uri=/users/user1234"
                    )
                ),
                Arguments.of(
                    503,
                    500,
                    "user12345",
                    ErrorResponse(
                        500,
                        "INTERNAL_SERVER_ERROR",
                        "Internal server error",
                        "uri=/users/user12345"
                    )
                )
            )
        }
    }


    @Test
    fun shouldReturnUserAndSaveFirstRequestInDb() {
        val mockedResponse =
            this::class.java.classLoader.getResourceAsStream("json/user.json")?.bufferedReader()
                ?.readText()
        val expected = User(
            12345,
            "user1",
            "User 1",
            "User",
            "https://avatars.githubusercontent.com/u/12345?v=4",
            OffsetDateTime.parse("2012-10-25T21:40:00Z"),
            21.36
        )
        val expectedUserRequest = UserRequest("user1", 1)

        MockServerClient(
            mockServer.host, mockServer.serverPort
        ).`when`(request().withPath("/users/user1")).respond(
            response().withBody(
                json(mockedResponse)
            )
        )
        val restTemplate = TestRestTemplate()
        val result =
            restTemplate.getForObject("http://localhost:$port/users/user1", User::class.java)

        val userRequest = userRequestRepository.findById("user1")
        
        assertThat(result).isEqualTo(expected).usingRecursiveComparison()
        assertThat(userRequest.isPresent)
        assertThat(userRequest.get()).isEqualTo(expectedUserRequest)
    }

    @Test
    fun shouldReturnUserAndSaveAnotherRequestInDb() {
        //prepare data
        userRequestRepository.save(UserRequest("user1", 4))
        val mockedResponse =
            this::class.java.classLoader.getResourceAsStream("json/user.json")?.bufferedReader()
                ?.readText()
        // expected values
        val expected = User(
            12345,
            "user1",
            "User 1",
            "User",
            "https://avatars.githubusercontent.com/u/12345?v=4",
            OffsetDateTime.parse("2012-10-25T21:40:00Z"),
            21.36
        )
        val expectedUserRequest = UserRequest("user1", 5)
        // when
        MockServerClient(
            mockServer.host, mockServer.serverPort
        ).`when`(request().withPath("/users/user1")).respond(
            response().withBody(
                json(mockedResponse)
            )
        )
        //execute
        val restTemplate = TestRestTemplate()
        val result =
            restTemplate.getForObject("http://localhost:$port/users/user1", User::class.java)

        //assert
        val userRequest = userRequestRepository.findById("user1")

        assertThat(result).isEqualTo(expected).usingRecursiveComparison()
        assertThat(userRequest.isPresent)
        assertThat(userRequest.get()).isEqualTo(expectedUserRequest)
    }

    @Test
    fun shouldReturn500CalculationNotPossible() {
        val mockedResponse =
            this::class.java.classLoader.getResourceAsStream("json/user_zero_followers.json")
                ?.bufferedReader()?.readText()
        val expected = ErrorResponse(
            500,
            "INTERNAL_SERVER_ERROR",
            "Calculation is not possible - followers number is 0",
            "uri=/users/user1zero"
        )

        MockServerClient(
            mockServer.host, mockServer.serverPort
        ).`when`(request().withPath("/users/user1zero")).respond(
            response().withBody(
                json(mockedResponse)
            )
        )
        val restTemplate = TestRestTemplate()
        val result = restTemplate.getForObject(
            "http://localhost:$port/users/user1zero", ErrorResponse::class.java
        )

        assertThat(result).usingRecursiveComparison().ignoringFields("timestamp")
            .isEqualTo(expected)
    }


    @ParameterizedTest(name = "Should return {1} error code when external service returns {0}")
    @MethodSource("provideExpectedErrorResponses")
    fun shouldReturnCorrectErrorResponse(
        statusCode: Int,
        expectedStatusCode: Int,
        login: String,
        expected: ErrorResponse
    ) {
        MockServerClient(
            mockServer.host, mockServer.serverPort
        ).`when`(request().withPath("/users/$login")).respond(
            response().withStatusCode(statusCode)
        )

        val restTemplate = TestRestTemplate()
        val result = restTemplate.getForObject(
            "http://localhost:$port/users/$login", ErrorResponse::class.java
        )
        assertThat(result).usingRecursiveComparison().ignoringFields("timestamp")
            .isEqualTo(expected)
    }
}


