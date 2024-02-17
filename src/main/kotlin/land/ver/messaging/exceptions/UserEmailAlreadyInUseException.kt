package land.ver.messaging.exceptions

import land.ver.messaging.exceptions.base.BadRequestException

class UserEmailAlreadyInUseException(emailAddress: String)
    : BadRequestException("A user with the email address \"$emailAddress\" already exists")
