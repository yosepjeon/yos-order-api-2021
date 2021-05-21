package com.yosep.order.data.entity

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import javax.jdo.annotations.Column
import javax.jdo.annotations.Inheritance
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Table("yos_order")
data class OrderEntity(
    @field:Id
    @field:Column(length = 300)
    val orderId: String,

    @field:Column(length = 300)
    val productId: String,

    @field:Column(length = 50)
    val senderId: String,

    @field:Column(length = 50)
    val senderName: String,

    @field:Column(length = 50)
    val receiverName: String,

    @field:Column(length = 50)
    val phone: String,

    @field:Column(length = 50)
    val postCode: String,

    @field:Column(length = 50)
    val roadAddr: String,

    @field:Column(length = 50)
    val jibunAddr: String,

    @field:Column(length = 50)
    val extraAddr: String,

    @field:Column(length = 50)
    val detailAddr: String,

    @field:Column(length = 50)
    val orderState: String,

    @field:Column
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val orderRegisterDate: LocalDateTime,

    @field:Column
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val orderUpdateDate: LocalDateTime,

    @field:Column
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val orderCompleteDate: LocalDateTime,
)
