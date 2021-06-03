package com.yosep.order.data.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Deprecated("더이상 사용하지 마시오.")
data class OrderDtoForCreationLegacy (
    var orderId: String,

    @field:NotNull
    @field:Size(max = 300)
    val productId: String,

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