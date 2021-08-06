package com.yosep.order.common.exception

import java.lang.RuntimeException

class StepFailException(override val message: String?): RuntimeException() {
}