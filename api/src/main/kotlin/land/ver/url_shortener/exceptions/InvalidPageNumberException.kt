package land.ver.url_shortener.exceptions

import land.ver.url_shortener.exceptions.base.BadRequestException

class InvalidPageNumberException(pageNumber: Number)
    : BadRequestException("\"$pageNumber\" is an invalid page number")
