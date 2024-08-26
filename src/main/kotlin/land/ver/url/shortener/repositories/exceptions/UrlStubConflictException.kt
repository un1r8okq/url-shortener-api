package land.ver.url.shortener.repositories.exceptions

import org.springframework.dao.DataIntegrityViolationException

class UrlStubConflictException(val stub: String, override val cause: DataIntegrityViolationException) :
    RuntimeException("The URL stub \"$stub\" already exists.", cause)
