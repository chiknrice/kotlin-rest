package org.chiknrice.rest

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AuthorsRepository : JpaRepository<AuthorEntity, Int> {

    @Cacheable(cacheNames = [AUTHORS_BY_ROLE])
    fun findByRole(role: Role): List<AuthorEntity>

    @CacheEvict(cacheNames = [AUTHORS_BY_ROLE], key = "#entity.role")
    override fun <S : AuthorEntity?> save(entity: S): S

    override fun findById(id: Int): Optional<AuthorEntity>

    companion object {
        const val AUTHORS_BY_ROLE = "authors_by_role"
    }
}