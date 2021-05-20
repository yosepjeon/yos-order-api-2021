package com.yosep.order.common

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class TestEntity(
    @field:NotNull
    val notNull: String = "",

    @field:NotEmpty
    val notEmpty: String = "",

    @field:NotBlank
    val notBlank: String = " "
)