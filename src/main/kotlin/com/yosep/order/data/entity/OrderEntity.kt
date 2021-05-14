package com.yosep.order.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import javax.jdo.annotations.Column
import javax.jdo.annotations.Inheritance
import javax.validation.constraints.NotNull

@Table("order")
data class OrderEntity(
    @Id
    @Column(length = 100)
    val productId: String,

    @NotNull
    @Column( length=100)
    val productName: String,

    @Column
    val productSale: Int,

    @NotNull
    @Column
    val productPrice: Int,

    @NotNull
    @Column
    val productQuantity: Int,

    @Column
    val productDetail: String,

    @NotNull
    @Column
    val productType: String,

    @NotNull
    @Column
    val productDetailType: String,

    @Column
    val productRdate: LocalDateTime,

    @Column
    val productUdate: LocalDateTime
)
