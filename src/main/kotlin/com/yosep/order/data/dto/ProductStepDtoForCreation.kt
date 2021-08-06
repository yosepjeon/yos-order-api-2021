package com.yosep.order.data.dto

import com.yosep.order.data.vo.OrderProductDtoForCreation
import org.springframework.hateoas.RepresentationModel
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class ProductStepDtoForCreation(
    @field:NotNull
    @field:Min(0)
    val totalPrice: Long,

    @field:NotEmpty
    @field:Size(min = 1)
    val orderProductDtos: List<OrderProductDtoForCreation>,

    @field:NotEmpty
    var state: String = "READY"
): RepresentationModel<ProductStepDtoForCreation>()