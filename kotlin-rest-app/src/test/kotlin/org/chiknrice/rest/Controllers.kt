package org.chiknrice.rest

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.cli.CliDocumentation
import org.springframework.restdocs.config.RestDocumentationConfigurer
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.headers.HeaderDocumentation.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.templates.TemplateFormats
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder

@RunWith(SpringRunner::class)
@WebMvcTest
@AutoConfigureRestDocs
@Import(RestDocsCustomizedConfiguration::class)
class AuthorsControllerTest {

    @get:Rule
    val restDocumentation = JUnitRestDocumentation()

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var authorsService: AuthorsService

    @Test
    fun testGetAuthors() {
        whenever(authorsService.getAuthors()).thenReturn(mapOf(
                Pair(1, Author("Juan Dela Cruz", Role.ADMIN)),
                Pair(2, Author("Rodrigo Duterte", Role.USER))
        ))

        mockMvc.perform(get("/authors").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andDo(document("get-authors",
                        responseFields(fieldWithPath("*").description("Author ID"))
                                .andWithPrefix("*.", *authorFields)
                ))
    }

    @Test
    fun testGetAuthor() {
        val id = 3

        whenever(authorsService.getAuthor(id)).thenReturn(Author("Lapu Lapu", Role.USER))

        mockMvc.perform(get("/authors/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andDo(document("get-author",
                        pathParameters(parameterWithName("id").description("Author's ID")),
                        responseFields(*authorFields)))
    }

    @Test
    fun testAuthorNotFound() {
        whenever(authorsService.getAuthor(3)).thenThrow(NotFoundException("Author with id 3 not found"))

        mockMvc.perform(get("/authors/3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound)
                .andDo(document("missing-author"))
    }

    @Test
    fun testCreateAuthor() {
        whenever(authorsService.create(any())).thenReturn(5)
        mockMvc.perform(post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Sarah Duterte\",\"role\":\"USER\"}"))
                .andExpect(status().isCreated)
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(document("create-author",
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("URI of the created author")
                        )))
    }

}

val authorFields = arrayOf(
        fieldWithPath("name").description("Author's Name"),
        fieldWithPath("role").description("Author's Role")
)

@TestConfiguration
class RestDocsCustomizedConfiguration : RestDocsMockMvcConfigurationCustomizer {
    override fun customize(configurer: MockMvcRestDocumentationConfigurer?) {
        configurer?.operationPreprocessors()
                ?.withRequestDefaults(prettyPrint())
                ?.withResponseDefaults(prettyPrint())
    }
}

@TestConfiguration
class MockMvcCustomizedConfiguration : MockMvcBuilderCustomizer {
    override fun customize(builder: ConfigurableMockMvcBuilder<*>?) {
//        builder.alwaysDo(
//                modifyResponseTo(prettyPrintContent()).andDocument(
//                        "{method-name}")).build()
    }
}