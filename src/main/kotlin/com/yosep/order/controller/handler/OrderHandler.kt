package com.yosep.order.controller.handler

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.validation.Validator

@Component
class OrderHandler @Autowired constructor(
    private val validator: Validator
) {


}