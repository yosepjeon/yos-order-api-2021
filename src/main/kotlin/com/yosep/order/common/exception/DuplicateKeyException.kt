package com.yosep.order.common.exception

import java.lang.RuntimeException

class DuplicateKeyException constructor(

): RuntimeException("Duplicate Key") {
}