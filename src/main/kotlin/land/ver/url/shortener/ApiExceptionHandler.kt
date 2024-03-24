package land.ver.url.shortener

import land.ver.url.shortener.exceptions.base.BadRequestException
import land.ver.url.shortener.exceptions.base.NotFoundException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.HashMap

@ControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errors = HashMap<String, String>()

        for (error in ex.bindingResult.fieldErrors) {
            errors[error.field] = error.defaultMessage ?: "Unknown"
        }

        for (error in ex.bindingResult.globalErrors) {
            errors[error.objectName] = error.defaultMessage ?: "Unknown"
        }

        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "One or more of your request parameters were invalid."
        )

        problemDetail.setProperty("invalidParameters", errors)

        return ResponseEntity(problemDetail, HttpStatus.BAD_REQUEST)
    }

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
        logger.error("Uncaught exception: $ex")
        return ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
