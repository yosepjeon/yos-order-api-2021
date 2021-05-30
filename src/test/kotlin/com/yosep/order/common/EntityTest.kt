package com.yosep.order.common

import com.yosep.order.common.data.orderStateMap
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.validation.Valid
import javax.validation.Validation

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class EntityTest @Autowired constructor(
    val springValidator: Validator
) {
    val log = Slf4JLoggerFactory.getInstance(EntityTest::class.java)

    @BeforeEach
    fun drawLineByTestBefore() {
        log.info("===================================================== START =====================================================")
    }

    @AfterEach
    fun drawLineByTestAfter() {
        log.info("===================================================== END =====================================================")
    }

    @Test
    @DisplayName("[Common] @NotNull, @NotEmpty, @NotBlank 테스트")
    fun entityConstraintAnnotationTest() {
        log.info("[Common] @NotNull, @NotEmpty, @NotBlank 테스트")
        val validator = Validation.buildDefaultValidatorFactory().validator

        val testEntity = TestEntity("", "", " ")
        val errors = BeanPropertyBindingResult(testEntity, "testEntity")

        springValidator.validate(testEntity, errors)
        val constraintValidations = validator.validate(testEntity)
        log.info("testEntity: $testEntity")
        log.info("constraintValidations: ${constraintValidations.size}")
        log.info("Errors: $errors")
    }

    @Test
    @DisplayName("[Common] OrderState 테스트")
    fun orderStateTest() {
        log.info("[Common] OrderState 테스트")
        val failValue = "COMPA"
        val successValue = "COMP"

        Assertions.assertEquals(true, orderStateMap[failValue] == null)
        Assertions.assertEquals(false, orderStateMap[successValue] == null)
    }

    @Test
    @DisplayName("ID 생성 테스트")
    fun ID_생성_테스트() {
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val uuid = UUID.randomUUID().toString().replace("-","")
        val id = "$now-$uuid"

        log.info(id)
    }
}