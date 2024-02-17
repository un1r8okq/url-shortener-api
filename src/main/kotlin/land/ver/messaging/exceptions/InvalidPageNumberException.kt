package land.ver.messaging.exceptions

import land.ver.messaging.exceptions.base.BadRequestException

class InvalidPageNumberException(pageNumber: Number)
    : BadRequestException("\"$pageNumber\" is an invalid page number")
