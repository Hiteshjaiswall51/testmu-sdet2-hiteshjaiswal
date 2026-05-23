package com.testmu.helper;

import org.testng.Assert;
import groovy.util.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Assertion {

    private static Logger Log = LogManager.getLogger(Assertion.class.getName());
    public void fail() {
        Log.info("Executing assertion: fail()");
        Assert.fail();
    }

    public void fail(String message) {
        Log.info("Executing assertion: fail with message - " + message);
        Assert.fail(message + "\n");
    }

    public void fail(String message, Throwable throwable) {
        Log.info("Executing assertion: fail with message - " + message + " | throwable - " + throwable, 2);
        Assert.fail(message + "\n", throwable);
    }

    public void assertTrue(boolean condition) {
        Log.info("Executing assertion: assertTrue with condition - " + condition, 2);
        try {
            Assert.assertTrue(condition);
            Log.info("Assertion passed: condition evaluated to true", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: condition evaluated to false", 2);
            throw assertionError;
        }
    }

    public void assertTrue(boolean condition, String customMessage) {
        Log.info("Executing assertion: assertTrue with condition - " + condition + " | message - " + customMessage, 2);
        try {
            Assert.assertTrue(condition, "'" + customMessage + "'");
            Log.info("Assertion passed: condition evaluated to true", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + customMessage, 2);
            throw assertionError;
        }
    }

    public void assertFalse(boolean condition) {
        Log.info("Executing assertion: assertFalse with condition - " + condition, 2);
        try {
            Assert.assertFalse(condition);
            Log.info("Assertion passed: condition evaluated to false", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: condition evaluated to true", 2);
            throw assertionError;
        }
    }

    public void assertFalse(boolean condition, String customMessage) {
        Log.info("Executing assertion: assertFalse with condition - " + condition + " | message - " + customMessage, 2);
        try {
            Assert.assertFalse(condition, "'" + customMessage + "'");
            Log.info("Assertion passed: condition evaluated to false", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + customMessage, 2);
            throw assertionError;
        }
    }

    public void assertEquals(int actual, int expected) {
        String message = String.format("Actual value [%s] is not equal to expected value [%s]", actual, expected);
        assertEquals(actual, expected, message);
    }

    public void assertEquals(int actual, int expected, String message) {
        Log.info("Executing assertion: assertEquals(int, int) | actual - " + actual + " | expected - " + expected + " | message - " + message, 2);
        try {
            Assert.assertEquals(actual, expected, message + "\n");
            Log.info("Assertion passed: actual value matched expected value", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + message, 2);
            throw assertionError;
        }
    }

    public void assertEquals(double actual, double expected) {
        String message = String.format("Actual value [%s] is not equal to expected value [%s]", actual, expected);
        assertEquals(actual, expected, message);
    }

    public void assertEquals(double actual, double expected, String message) {
        Log.info("Executing assertion: assertEquals(double, double) | actual - " + actual + " | expected - " + expected + " | message - " + message, 2);
        try {
            Assert.assertEquals(actual, expected, message + "\n");
            Log.info("Assertion passed: actual value matched expected value", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + message, 2);
            throw assertionError;
        }
    }

    public void assertEquals(double actual, double expected, double delta, String message) {
        Log.info("Executing assertion: assertEquals(double, double, delta) | actual - " + actual + " | expected - " + expected + " | delta - " + delta + " | message - " + message, 2);
        try {
            Assert.assertEquals(actual, expected, delta, message + "\n");
            Log.info("Assertion passed: actual value matched expected value within delta", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + message, 2);
            throw assertionError;
        }
    }

    public void assertEquals(String actual, String expected) {
        String message = String.format("Actual value [%s] is not equal to expected value [%s]", actual, expected);
        assertEquals(actual, expected, message);
    }

    public void assertEquals(String actual, String expected, String message) {
        Log.info("Executing assertion: assertEquals(String, String) | actual - " + actual + " | expected - " + expected + " | message - " + message, 2);
        try {
            Assert.assertEquals(actual, expected, message + "\n");
            Log.info("Assertion passed: actual value matched expected value", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + message, 2);
            throw assertionError;
        }
    }

    public void assertEquals(boolean actual, boolean expected) {
        String message = String.format("Actual value [%s] is not equal to expected value [%s]", actual, expected);
        assertEquals(actual, expected, message);
    }

    public void assertEquals(boolean actual, boolean expected, String message) {
        Log.info("Executing assertion: assertEquals(boolean, boolean) | actual - " + actual + " | expected - " + expected + " | message - " + message, 2);
        try {
            Assert.assertEquals(actual, expected, message + "\n");
            Log.info("Assertion passed: actual value matched expected value", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + message, 2);
            throw assertionError;
        }
    }

    public void assertEquals(List<String> actual, List<String> expected) {
        String message = String.format("Actual value [%s] is not equal to expected value [%s]", actual, expected);
        assertEquals(actual, expected, message);
    }

    public void assertEquals(List<String> actual, List<String> expected, String message) {
        Log.info("Executing assertion: assertEquals(List, List) | actual - " + actual + " | expected - " + expected + " | message - " + message, 2);
        try {
            Assert.assertEquals(actual, expected, message + "\n");
            Log.info("Assertion passed: actual list matched expected list", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + message, 2);
            throw assertionError;
        }
    }

    public void assertEquals(Object actual, Object expected) {
        String message = String.format("Actual value [%s] is not equal to expected value [%s]", actual, expected);
        assertEquals(actual, expected, message);
    }

    public void assertEquals(Object actual, Object expected, String message) {
        Log.info("Executing assertion: assertEquals(Object, Object) | actual - " + actual + " | expected - " + expected + " | message - " + message, 2);
        try {
            Assert.assertEquals(actual, expected, message + "\n");
            Log.info("Assertion passed: actual value matched expected value", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + message, 2);
            throw assertionError;
        }
    }

    public void assertNotEquals(int actual, int expected) {
        String message = String.format("Actual value [%s] is equal to expected value [%s]", actual, expected);
        assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(int actual, int expected, String message) {
        Log.info("Executing assertion: assertNotEquals(int, int) | actual - " + actual + " | expected - " + expected + " | message - " + message, 2);
        try {
            Assert.assertNotEquals(actual, expected, message + "\n");
            Log.info("Assertion passed: actual value did not match expected value", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + message, 2);
            throw assertionError;
        }
    }

    public void assertNotEquals(double actual, double expected) {
        String message = String.format("Actual value [%s] is equal to expected value [%s]", actual, expected);
        assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(double actual, double expected, String message) {
        Log.info("Executing assertion: assertNotEquals(double, double) | actual - " + actual + " | expected - " + expected + " | message - " + message, 2);
        try {
            Assert.assertNotEquals(actual, expected, message + "\n");
            Log.info("Assertion passed: actual value did not match expected value", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + message, 2);
            throw assertionError;
        }
    }

    public void assertNotEquals(String actual, String expected) {
        String message = String.format("Actual value [%s] is equal to expected value [%s]", actual, expected);
        assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(String actual, String expected, String message) {
        Log.info("Executing assertion: assertNotEquals(String, String) | actual - " + actual + " | expected - " + expected + " | message - " + message, 2);
        try {
            Assert.assertNotEquals(actual, expected, message + "\n");
            Log.info("Assertion passed: actual value did not match expected value", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + message, 2);
            throw assertionError;
        }
    }

    public void assertNotEquals(boolean actual, boolean expected) {
        String message = String.format("Actual value [%s] is equal to expected value [%s]", actual, expected);
        assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(boolean actual, boolean expected, String message) {
        Log.info("Executing assertion: assertNotEquals(boolean, boolean) | actual - " + actual + " | expected - " + expected + " | message - " + message, 2);
        try {
            Assert.assertNotEquals(actual, expected, message + "\n");
            Log.info("Assertion passed: actual value did not match expected value", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + message, 2);
            throw assertionError;
        }
    }

    public void assertNotEquals(Object actual, Object expected) {
        String message = String.format("Actual value [%s] is equal to expected value [%s]", actual, expected);
        assertNotEquals(actual, expected, message);
    }

    public void assertNotEquals(Object actual, Object expected, String message) {
        Log.info("Executing assertion: assertNotEquals(Object, Object) | actual - " + actual + " | expected - " + expected + " | message - " + message, 2);
        try {
            Assert.assertNotEquals(actual, expected, message + "\n");
            Log.info("Assertion passed: actual value did not match expected value", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + message, 2);
            throw assertionError;
        }
    }

    public void assertNull(Object object) {
        String message = String.format("Expected object to be null but found [%s]", object);
        assertNull(object, message);
    }

    public void assertNull(Object object, String message) {
        Log.info("Executing assertion: assertNull | object - " + object + " | message - " + message, 2);
        try {
            Assert.assertNull(object, message + "\n");
            Log.info("Assertion passed: object is null", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + message, 2);
            throw assertionError;
        }
    }

    public void assertNotNull(Object object) {
        String message = "Expected object to be non-null";
        assertNotNull(object, message);
    }

    public void assertNotNull(Object object, String message) {
        Log.info("Executing assertion: assertNotNull | object - " + object + " | message - " + message, 2);
        try {
            Assert.assertNotNull(object, message + "\n");
            Log.info("Assertion passed: object is not null", 2);
        } catch (AssertionError assertionError) {
            Log.info("Assertion failed: " + message, 2);
            throw assertionError;
        }
    }
}
