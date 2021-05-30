package com.yosep.order.data.entity

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import javax.jdo.annotations.Column




@Table("yos_order")
data class Order(
    @Id
    @field:Column(length = 300)
    var orderId: String = "",

    @field:Column(length = 300)
    var productId: String = "",

    @field:Column(length = 50)
    var senderId: String = "",

    @field:Column(length = 50)
    var senderName: String = "",

    @field:Column(length = 50)
    var receiverName: String = "",

    @field:Column(length = 50)
    var phone: String = "",

    @field:Column(length = 50)
    var postCode: String = "",

    @field:Column(length = 50)
    var roadAddr: String = "",

    @field:Column(length = 50)
    var jibunAddr: String = "",

    @field:Column(length = 50)
    var extraAddr: String = "",

    @field:Column(length = 50)
    var detailAddr: String = "",

    @field:Column(length = 50)
    var orderState: String = "",

    @field:Column
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var orderRegisterDate: LocalDateTime = LocalDateTime.now(),

    @field:Column
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var orderUpdateDate: LocalDateTime? = null,

    @field:Column
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var orderCompleteDate: LocalDateTime? = null,
): Persistable<String> {

    @Transient
    private var isNew = false

    @Transient
    override fun isNew(): Boolean {
        return isNew || orderId == null
    }

    fun setAsNew(): Order? {
        isNew = true
        return this
    }

    override fun getId(): String? {
        return this.orderId
    }
}
