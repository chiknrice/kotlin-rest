package org.chiknrice.rest

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class AuthorEntity(@Id @GeneratedValue val id:Int, val name: String, val role: Role)