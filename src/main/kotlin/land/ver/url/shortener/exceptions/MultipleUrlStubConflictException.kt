package land.ver.url.shortener.exceptions

class MultipleUrlStubConflictException(
    attempts: Int,
    stubsAttempted: List<String>,
) :
    RuntimeException(
        "Unable to create a URL due to URL stub conflicts after $attempts attempts " +
            "with stubs \"${stubsAttempted.joinToString("\", \"")}\""
    )
