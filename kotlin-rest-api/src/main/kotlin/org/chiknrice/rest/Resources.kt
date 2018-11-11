package org.chiknrice.rest

import org.springframework.hateoas.ExposesResourceFor
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

enum class Role { ADMIN, USER }

data class Author(val name: String, val role: Role)

data class Post(val subject: String, val contents: String, val author: Author, val created: Date)


@RequestMapping(path = ["/authors"], produces = [MediaType.APPLICATION_JSON_VALUE])
@ExposesResourceFor(Author::class)
interface AuthorsAPI {

    @GetMapping
    fun getAuthors(): ResponseEntity<Map<Int, Author>>

    @GetMapping(path = ["/{id}"])
    fun getAuthor(@PathVariable id: Int): ResponseEntity<Author>

    @PostMapping
    fun createAuthor(@RequestBody author: Author): ResponseEntity<Void>

}