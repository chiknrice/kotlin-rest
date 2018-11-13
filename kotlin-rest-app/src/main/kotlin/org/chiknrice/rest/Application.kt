package org.chiknrice.rest

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.hateoas.config.EnableEntityLinks

@SpringBootApplication
@EnableEntityLinks
@EnableCaching
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}