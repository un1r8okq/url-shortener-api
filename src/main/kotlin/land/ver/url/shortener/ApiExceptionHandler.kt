package land.ver.url.shortener

import land.ver.url.shortener.exceptions.base.BadRequestException
import land.ver.url.shortener.exceptions.base.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.servlet.resource.NoResourceFoundException

@ControllerAdvice
class ApiExceptionHandler {
    private val defaultValidationError = "Validation error"
    private val logger = LoggerFactory.getLogger(ApiExceptionHandler::class.java)

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun methodValidationHandler(ex: HandlerMethodValidationException): ProblemDetail {
        val detail = ex.valueResults.firstOrNull()?.resolvableErrors?.firstOrNull()?.defaultMessage

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail ?: defaultValidationError)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun missingRequestParamHandler(ex: MissingServletRequestParameterException): ProblemDetail {
        val detail = ex.body.detail

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail ?: defaultValidationError)
    }

    /**
     * Catch routes with no matching controller
     */
    @ExceptionHandler(NoResourceFoundException::class)
    fun noResourceFoundHandler() = ProblemDetail.forStatus(HttpStatus.NOT_FOUND)

    @ExceptionHandler(NotFoundException::class)
    fun userNotFoundHandler(ex: NotFoundException) =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.message ?: "",
        )

    @ExceptionHandler(BadRequestException::class)
    fun invalidRequest(ex: BadRequestException) =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.message ?: "",
        )

    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception): ResponseEntity<Any> {
        logger.error(ex.message)
        return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
