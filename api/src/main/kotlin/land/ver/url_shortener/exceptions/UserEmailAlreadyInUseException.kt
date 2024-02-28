package land.ver.url_shortener.exceptions

import land.ver.url_shortener.exceptions.base.BadRequestException

class UserEmailAlreadyInUseException(emailAddress: String)
    : BadRequestException("A user with the email address \"$emailAddress\" already exists")
