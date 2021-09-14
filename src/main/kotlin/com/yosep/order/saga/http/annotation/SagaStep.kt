package com.yosep.order.saga.http.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(
    AnnotationTarget.FUNCTION
)
@Retention(
    RetentionPolicy.RUNTIME
)
annotation class SagaStep