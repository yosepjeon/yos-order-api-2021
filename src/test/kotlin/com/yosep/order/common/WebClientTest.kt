package com.yosep.order.common

import com.yosep.order.data.entity.OrderTest
import com.yosep.order.data.repository.OrderTestRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient

import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.test.context.junit.jupiter.SpringExtension

import org.springframework.restdocs.RestDocumentationContextProvider

import org.springframework.web.context.WebApplicationContext

import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields



@SpringBootTest
@ExtendWith(RestDocumentationExtension::class, SpringExtension::class)
//@AutoConfigureWebTestClient
class WebClientTest @Autowired constructor(
    private val orderTestRepository: OrderTestRepository,
    private val context: ApplicationContext
) {
    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun createOrderTestEntity() {
        val orderTest = OrderTest("test","테스트")
        orderTest.setAsNew()

        orderTestRepository.save(orderTest)
            .subscribe()
    }

    @AfterEach
    fun deleteOrderTestEntity() {
        val orderId = "test"
        orderTestRepository.deleteById(orderId)
            .subscribe()
    }

    @BeforeEach
    fun setup(
        webApplicationContext: WebApplicationContext?,
        restDocumentation: RestDocumentationContextProvider?
    ) {
        webTestClient = WebTestClient.bindToApplicationContext(context)
            .configureClient()
            .filter(
                documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withRequestDefaults()
                    .withResponseDefaults(prettyPrint())
            )
            .build()
    }

    @Test
    fun webClientConnectionTest() {
        webTestClient.get()
            .uri("/product/test/connection-test")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun restDocTest() {
        webTestClient.get()
            .uri("/product/test/rest-doc-test")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(document(
                "test-restdoc",
                RestApiDocumentation.getDocumentRequest(),
                RestApiDocumentation.getDocumentResponse(),
                requestHeaders(),
                responseHeaders(),
                responseFields(
                    fieldWithPath("orderId").type(JsonFieldType.STRING).description("상품 아이디"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("상품 이름"),
                    fieldWithPath("isNew").type(JsonFieldType.BOOLEAN).description("생성된 상품인지"),
                    fieldWithPath("id").type(JsonFieldType.STRING).description("상품 아이디 자체"),
                    )
            ))
    }
}


//@SpringBootTest
//@ExtendWith(RestDocumentationExtension::class, SpringExtension::class)
//internal class SomeControllerDocumentation {
//    private var testClient: WebTestClient? = null
//
//    @MockBean
//    private val service: SomeService? = null
//    @BeforeEach
//    fun setUp(
//        webApplicationContext: WebApplicationContext?,
//        restDocumentation: RestDocumentationContextProvider?
//    ) {
//        testClient = WebTestClient.bindToApplicationContext(webApplicationContext!!)
//            .configureClient()
//            .filter(documentationConfiguration(restDocumentation))
//            .build()
//    }
//
//    @Test
//    fun testGetAllEndpoint() {
//        `when`(service.getMachines())
//            .thenReturn(List.of(Machine(1, "Machine 1", "192.168.1.5", 9060)))
//        testClient!!.get().uri("/api/machines")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange().expectStatus().isOk
//            .expectBody().consumeWith(document("machines-list"))
//    }
//}