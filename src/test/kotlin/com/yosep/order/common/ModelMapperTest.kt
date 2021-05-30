package com.yosep.order.common

import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.entity.Order
import com.yosep.order.order.data.repository.OrderRepositoryTest
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.*
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class ModelMapperTest @Autowired constructor(
    val orderDtoForCreationToOrderMapper: ModelMapper
) {
    val log = Slf4JLoggerFactory.getInstance(OrderRepositoryTest::class.java)

    @BeforeEach
    fun printStart() {
        log.info("===================================================== START =====================================================")
    }

    @AfterEach
    fun printEnt() {
        log.info("===================================================== END =====================================================")
    }

    @Test
    @DisplayName("orderDto에서 order로 변환 테스트")
    fun orderDtoForCreation에서_order로_변환_테스트() {
        log.info("orderDto에서 order로 변환 테스트")

        val orderDtoForCreation = OrderDtoForCreation(
            "create-order-test",
            "product-01",
            "sender1",
            "요깨비",
            "이재훈",
            "123123123",
            "asdf",
            "asdf",
            "asdf",
            "asdf",
            "asdf",
            "READY",
        )

        val convertedOrder = orderDtoForCreationToOrderMapper.map(orderDtoForCreation, Order::class.java)
        log.info(convertedOrder.toString())
    }
}