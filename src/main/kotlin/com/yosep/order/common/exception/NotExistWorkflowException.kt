package com.yosep.order.common.exception

import java.lang.RuntimeException

class NotExistWorkflowException constructor(

): RuntimeException("Not Exist Workflow") {
}