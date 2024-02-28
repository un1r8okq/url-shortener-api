package land.ver.url_shortener.controllers

import jakarta.validation.Valid
import land.ver.url_shortener.UserRepository
import land.ver.url_shortener.exceptions.InvalidPageNumberException
import land.ver.url_shortener.exceptions.UserEmailAlreadyInUseException
import land.ver.url_shortener.exceptions.UserNotFoundException
import land.ver.url_shortener.http.ApiResult
import land.ver.url_shortener.http.PagedApiResult
import land.ver.url_shortener.models.User
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/users")
class UserController(val db: UserRepository) {
    @GetMapping("", "/")
    fun index(@RequestParam pageNumber: Int): PagedApiResult<User> {
        if (pageNumber < 1) {
            throw InvalidPageNumberException(pageNumber)
        }

        val pageable = Pageable.ofSize(10).withPage(pageNumber - 1)
        val pageResult = db.findAll(pageable)

        return PagedApiResult(pageResult)
    }

    @GetMapping("/{id}")
    fun find(@PathVariable id: UUID): ApiResult<User> {
        val result = db.findById(id)

        if (result.isEmpty) {
            throw UserNotFoundException(id)
        }

        return ApiResult(result.get())
    }

    @PostMapping("")
    fun create(@Valid @RequestBody user: User): ResponseEntity<Any>
    {
        if (db.existsByEmailAddress(user.emailAddress)) {
            throw UserEmailAlreadyInUseException(user.emailAddress)
        }

        db.save(user)

        return ResponseEntity(HttpStatus.CREATED)
    }
}
