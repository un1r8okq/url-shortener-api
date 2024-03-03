package land.ver.url.shortener.exceptions

import land.ver.url.shortener.exceptions.base.BadRequestException

class UserEmailAlreadyInUseException(emailAddress: String) :
    BadRequestException("A user with the email address \"$emailAddress\" already exists")
