package com.yosep.order.data.enum

enum class OrderProductState(val value: String) {
    READY("READY"),
    PROCEEDING("PROCEEDING"),
    PENDING("PENDING"),
    FAIL("FAIL"),
    COMP("COMP")
}