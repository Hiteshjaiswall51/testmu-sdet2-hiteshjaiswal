package com.testmu.listner;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RetryListener implements IRetryAnalyzer {

    private final Map<String, Integer> retryCounts = new ConcurrentHashMap<>();

    @Override
    public boolean retry(ITestResult result) {// check if the test method had RetryCountIfFailed annotation
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        RetryCountIfFailed annotation = method.getAnnotation(RetryCountIfFailed.class);
        // based on the value of annotation see if test needs to be rerun
        if (annotation != null && annotation.value() > 0) {
            String retryKey = method.toGenericString() + Arrays.deepToString(result.getParameters());
            int retryCount = retryCounts.getOrDefault(retryKey, 0);
            if (retryCount < annotation.value()) {
                int nextRetryCount = retryCount + 1;
                retryCounts.put(retryKey, nextRetryCount);
                String retryMessage = String.format("Retrying %s. Attempt %d of %d",
                        result.getMethod().getQualifiedName(), nextRetryCount, annotation.value());
                Reporter.log(retryMessage, true);
                result.setAttribute("retryAttempt", nextRetryCount);
                result.setAttribute("maxRetryCount", annotation.value());
                return true;
            }
        }
        return false;
    }
}
