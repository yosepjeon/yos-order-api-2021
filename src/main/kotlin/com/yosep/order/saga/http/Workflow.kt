package com.yosep.order.saga.http

import java.time.LocalDateTime

//interface Workflow<T,R> {
//    val id: String
//    val steps: MutableList<WorkflowStep<T,R>>
//    val type: String
//
//    fun getId() {
//
//    }
//}

/*
* 추상 클래스이기 때문에 상위 클래스로서 타입을 자유롭게 사용할 수 없음... open으로 바꿔보자.
 */
//abstract class Workflow<T,R>(
//    private val id: String,
//    private val steps: MutableList<WorkflowStep<T,R>>,
//    private val type: String,
//    private val state: String
//) {
//    fun getId():String = this.id
//    fun getSteps(): MutableList<WorkflowStep<T,R>> = this.steps
//    fun getType(): String = this.type
//}

open class Workflow<T, R>(
    var id: String,
    val steps: MutableList<WorkflowStep<Any>>,
    val type: String,
    var state: String = "READY",
    val createdDate: LocalDateTime = LocalDateTime.now()
) {
//    fun getId():String = this.id
//    fun getSteps(): MutableList<WorkflowStep<T,R>> = this.steps
//    fun getType(): String = this.type
//    fun getState(): String = this.state
//    fun getCreatedDate(): LocalDateTime = this.createdDate
}