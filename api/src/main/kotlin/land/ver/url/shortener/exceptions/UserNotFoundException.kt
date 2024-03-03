package land.ver.url.shortener.exceptions

import land.ver.url.shortener.exceptions.base.NotFoundException
import java.util.UUID

class UserNotFoundException(id: UUID) :
    NotFoundException("A user with the ID \"$id\" does not exist")
