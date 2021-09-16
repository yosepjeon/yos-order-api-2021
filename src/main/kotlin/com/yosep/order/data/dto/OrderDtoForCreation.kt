package com.yosep.order.data.dto

import com.yosep.order.data.vo.OrderProductDiscountCouponDto
import com.yosep.order.data.vo.OrderProductDtoForCreation
import com.yosep.order.data.vo.OrderTotalDiscountCouponDto
import javax.validation.constraints.*

data class OrderDtoForCreation(
    var orderId: String,

    @field:NotNull
    @field:Min(0)
    val totalPrice: Long,

    @field:NotEmpty
    @field:Size(min = 1)
    val orderProductDtos: List<OrderProductDtoForCreation> = emptyList(),

    @field:NotEmpty
    @field:Size(min = 1)
    val orderProductDiscountCouponDtos: List<OrderProductDiscountCouponDto> = emptyList(),

    @field:NotEmpty
    @field:Size(min = 0)
    val orderTotalDiscountCouponDtos: List<OrderTotalDiscountCouponDto> = emptyList(),

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
    var orderState: String
)