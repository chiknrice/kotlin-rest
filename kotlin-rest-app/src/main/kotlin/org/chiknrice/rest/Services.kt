package org.chiknrice.rest

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ResponseStatus

interface AuthorsService {

    fun getAuthors(): Map<Int, Author>

    fun getAuthor(id: Int): Author

    fun create(author: Author): Int

}

@Service
class AuthorsServiceProd : AuthorsService {

    private val authors = arrayOf(Author("Ian Bondoc", Role.ADMIN),
            Author("Jen Legaspi", Role.USER))

    override fun getAuthors() = authors.mapIndexed { index, author -> index to author }.toMap()

    override fun getAuthor(id: Int) = authors.getOrElse(id) { throw NotFoundException("Author with id: $id not found") }

    override fun create(author: Author): Int {
        authors.set(authors.size, author)
        return authors.size - 1
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