package land.ver.messaging.exceptions

import land.ver.messaging.exceptions.base.NotFoundException
import java.util.UUID

class UserNotFoundException(id: UUID)
    : NotFoundException("A user with the ID \"$id\" does not exist")
