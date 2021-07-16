package com.yosep.order.data.dto

import com.yosep.order.data.vo.OrderProductDtoForCreation
import javax.validation.constraints.*

data class OrderDtoForCreation(
    @field:NotNull
    @field:Min(0)
    val totalPrice: Long,

    @field:NotEmpty
    @field:Size(min = 1)
    val orderProductDtos: List<OrderProductDtoForCreation>,

    @field:NotBlank
    @field:Size(max = 50)
    val userId: String,

    @field:NotBlank
    @field:Size(max = 50)
    val senderName: String,

    @field:NotBlank
    @field:Size(max = 50)
    val receiverName: String,

    @field:NotBlank
    @field:Size(max = 50)
    val phone: String,

    @field:NotBlank
    @field:Size(max = 50)
    val postCode: String,

    @field:NotBlank
    @field:Size(max = 50)
    val roadAddr: String,

    @field:NotBlank
    @field:Size(max = 50)
    val jibunAddr: String,

    @field:NotBlank
    @field:Size(max = 50)
    val extraAddr: String,

    @field:Size(max = 50)
    val detailAddr: String,

    @field:Size(max = 50)
    val orderState: String
)