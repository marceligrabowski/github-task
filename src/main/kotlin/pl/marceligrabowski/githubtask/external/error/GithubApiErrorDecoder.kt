package pl.marceligrabowski.githubtask.external.error

import feign.Response
import feign.codec.ErrorDecoder
import pl.marceligrabowski.githubtask.external.error.exception.ExternalServiceInternalErrorException
import pl.marceligrabowski.githubtask.external.error.exception.ExternalServiceUnavailableException
import pl.marceligrabowski.githubtask.external.error.exception.UserNotFoundException


class GithubApiErrorDecoder : ErrorDecoder {
    //todo: unit tests
    override fun decode(methodKey: String?, response: Response?): Exception {
        // basic error handling
        return when (response!!.status()) {
            404 -> UserNotFoundException()
            500 -> ExternalServiceInternalErrorException()
            503 -> ExternalServiceUnavailableException()
            else -> RuntimeException("Generic external service error")
        }
    }
}