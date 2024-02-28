package land.ver.url_shortener.http

data class BadRequestResponseBody(val errors: Map<String, String>)