package org.chiknrice.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.ResponseStatus

interface AuthorsService {

    fun getAuthors(): Map<Int, Author>

    fun getAuthor(id: Int): Author

    fun create(author: Author): Int

    @Transactional
    fun createAll(authors: List<Author>): List<Int>

}

@Service
class AuthorsServiceProd : AuthorsService {

    @Autowired
    lateinit var authorsRepository: AuthorsRepository

    override fun getAuthors() = authorsRepository
            .findByRole(Role.ADMIN)
            .map { authorEntity -> authorEntity.id to Author(authorEntity.name, authorEntity.role) }
            .toMap()

    override fun getAuthor(id: Int) = authorsRepository
            .findById(id)
            .map { authorEntity -> Author(authorEntity.name, authorEntity.role) }
            .orElseThrow { NotFoundException("Author with id: $id not found") }

    override fun create(author: Author) = authorsRepository.save(AuthorEntity(-1, author.name, author.role)).id

    override fun createAll(authors: List<Author>): List<Int> {
        return authors.map { author ->
            if(author.role == Role.USER) {
                throw RuntimeException()
            } else {
                println("Saving ${author.name}")
                authorsRepository.save(AuthorEntity(-1, author.name, author.role)).id
            }
        }.toList()
    }

}

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(message, cause, enableSuppression, writableStackTrace)
}