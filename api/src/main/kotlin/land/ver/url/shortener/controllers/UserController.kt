package land.ver.url.shortener.controllers

import jakarta.validation.Valid
import land.ver.url.shortener.API_PAGE_SIZE
import land.ver.url.shortener.dtos.ApiResult
import land.ver.url.shortener.dtos.PagedApiResult
import land.ver.url.shortener.exceptions.InvalidPageNumberException
import land.ver.url.shortener.exceptions.UserEmailAlreadyInUseException
import land.ver.url.shortener.exceptions.UserNotFoundException
import land.ver.url.shortener.models.User
import land.ver.url.shortener.repositories.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@RestController
@RequestMapping("/api/v1/users")
class UserController(val db: UserRepository) {
    @GetMapping("", "/")
    fun index(@RequestParam pageNumber: Int): PagedApiResult<User> {
        if (pageNumber < 1) {
            throw InvalidPageNumberException(pageNumber)
        }

        val pageable = Pageable.ofSize(API_PAGE_SIZE).withPage(pageNumber - 1)
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
    fun create(@Valid @RequestBody user: User): ResponseEntity<Any> {
        if (db.existsByEmailAddress(user.emailAddress)) {
            throw UserEmailAlreadyInUseException(user.emailAddress)
        }

        db.save(user)

        return ResponseEntity(HttpStatus.CREATED)
    }
}
