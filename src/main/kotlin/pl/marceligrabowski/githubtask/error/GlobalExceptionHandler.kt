package pl.marceligrabowski.githubtask.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pl.marceligrabowski.githubtask.error.exception.CalculationNotPossibleException
import pl.marceligrabowski.githubtask.error.models.ErrorResponse
import pl.marceligrabowski.githubtask.external.error.exception.ExternalServiceInternalErrorException
import pl.marceligrabowski.githubtask.external.error.exception.ExternalServiceUnavailableException
import pl.marceligrabowski.githubtask.external.error.exception.UserNotFoundException

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(
        exception: UserNotFoundException, request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity<ErrorResponse>(
            ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.name,
                exception.message,
                request.getDescription(false),
            ), HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(ExternalServiceInternalErrorException::class)
    fun handleExternalServiceInternalErrorException(
        exception: ExternalServiceInternalErrorException, request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        // we don't want to tell user that it's about failure of dependent service
        return ResponseEntity<ErrorResponse>(
            ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name,
                "Internal server error",
                request.getDescription(false),
            ), HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ExceptionHandler(ExternalServiceUnavailableException::class)
    fun handleExternalServiceUnavailableException(
        exception: ExternalServiceUnavailableException, request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        // we don't want to tell user that it's about failure of dependent service
        return ResponseEntity<ErrorResponse>(
            ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name,
                "Internal server error",
                request.getDescription(false),
            ), HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ExceptionHandler(CalculationNotPossibleException::class)
    fun handleCalculationNotPossibleException(
        exception: CalculationNotPossibleException, request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity<ErrorResponse>(
            ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name,
                exception.message,
                request.getDescription(false),
            ), HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}