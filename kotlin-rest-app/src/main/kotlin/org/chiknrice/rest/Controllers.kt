package org.chiknrice.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.EntityLinks
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class AuthorsController : AuthorsAPI {

    @Autowired
    lateinit var authorsService: AuthorsService

    @Autowired
    lateinit var entityLinks: EntityLinks

    override fun getAuthors() = ok(authorsService.getAuthors())

    override fun getAuthor(id: Int) = ok(authorsService.getAuthor(id))

    override fun createAuthor(author: Author): ResponseEntity<Void> {
        val generatedID = authorsService.create(author)
        val location = entityLinks.linkToSingleResource(Author::class.java, generatedID).expand().href
        return created(URI.create(location)).build()
    }

}
