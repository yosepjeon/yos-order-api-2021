package com.yosep.order.common.validation

import com.yosep.order.data.dto.OrderDtoForCreation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import org.springframework.web.server.ServerWebInputException

@Component
class OrderValidator @Autowired constructor(
    private val validator: Validator
) {
    fun validateOrderDtoForCreation(orderDtoForCreation: OrderDtoForCreation) {
        val errors = BeanPropertyBindingResult(orderDtoForCreation, "orderDtoForCreation")
        validator.validate(orderDtoForCreation, errors)

        if(errors.hasErrors()) {
            throw ServerWebInputException(errors.toString())
        }
    }
}