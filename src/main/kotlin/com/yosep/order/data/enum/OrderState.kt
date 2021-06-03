package com.yosep.order.data.enum

enum class OrderState(val value: String) {
    READY("READY"),
    PROCEEDING("PROCEEDING"),
    PENDING("PENDING"),
    FAIL("FAIL"),
    COMP("COMP")
}