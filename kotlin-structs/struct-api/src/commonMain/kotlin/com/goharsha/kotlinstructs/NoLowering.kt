package com.goharsha.kotlinstructs

@Target(
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CLASS,
    AnnotationTarget.FILE
)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
public annotation class NoLowering
