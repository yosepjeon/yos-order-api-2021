package com.yosep.order.common

import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator
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
        val validator = Validation.buildDefaultValidatorFactory().validator

        val testEntity = TestEntity("", " ", " ")
        val errors = BeanPropertyBindingResult(testEntity, "testEntity")

        springValidator.validate(testEntity, errors)
        val constraintValidations = validator.validate(testEntity)
        log.info("testEntity: $testEntity")
        log.info("constraintValidations: ${constraintValidations.size}")
        log.info("Errors: $errors")
    }
}