package land.ver.url.shortener.exceptions

import land.ver.url.shortener.exceptions.base.BadRequestException

class InvalidPageNumberException(pageNumber: Number) :
    BadRequestException("\"$pageNumber\" is an invalid page number")
