package com.yosep.order.common.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper
interface OrderMapper {
    companion object {
        val INSTANCE = Mappers.getMapper(OrderMapper::class.java)
    }

}