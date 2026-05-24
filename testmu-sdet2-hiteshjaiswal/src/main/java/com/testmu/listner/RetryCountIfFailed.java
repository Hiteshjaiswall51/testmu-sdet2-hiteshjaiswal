package com.testmu.listner;

public @interface RetryCountIfFailed {
    int value() default 0;
}
