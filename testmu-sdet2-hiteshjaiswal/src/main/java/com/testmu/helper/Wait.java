package com.testmu.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import com.testmu.utils.Pause;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Wait {
    private final PageHolder pageHolder = new PageHolder();
    private static Logger Log = LogManager.getLogger(Wait.class.getName());

    public void waitTillElementIsVisibleAndClickable(WebElement element) {
        WebDriverWait wait = new WebDriverWait(pageHolder.getDriver(), Pause.duration(Pause.SMALL));
        wait.until(ExpectedConditions.visibilityOf(element));
        wait = new WebDriverWait(pageHolder.getDriver(), Pause.duration(Pause.SMALL));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        Log.info("Element is visible and clickable");
    }

    public void waitForElementToDisAppear(List<WebElement> elements) {
        WebDriverWait wait = new WebDriverWait(pageHolder.getDriver(), Pause.duration(Pause.LOW));
        wait.until(ExpectedConditions.invisibilityOfAllElements(elements));
        Log.info("Elements disappeared from screen");
    }

    public WebElement waitForElementToDisplay(WebElement element, long maxTime) {
        try {
            WebDriverWait wait = new WebDriverWait(pageHolder.getDriver(), Duration.ofSeconds(maxTime));
            wait.until(ExpectedConditions.visibilityOf(element));
            Log.info("Element is visible on screen"+ element);
        } catch (Exception e) {
            Log.error("Element "+element+ " is not available on screen to perform action");
        }
        return element;
    }

    public WebElement waitForElementToDisplay(String xpath, long maxTime) {
        try {
            WebDriverWait wait = new WebDriverWait(pageHolder.getDriver(), Duration.ofSeconds(maxTime));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            Log.info("Element is visible on screen"+ xpath);
        } catch (Exception e) {
            Log.error("Element "+xpath+ " is not available on screen to perform action");
        }
        return null;
    }

    public boolean fluentWaitForElementToDisplay(WebElement element, long maxTime) {
        try {
            org.openqa.selenium.support.ui.Wait<WebElement> fluentWait = new FluentWait<>(element)
                    .withTimeout(Duration.ofSeconds(maxTime))
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(NoSuchElementException.class);

            fluentWait.until(WebElement::getTagName);
            Log.info("Element is visible using fluent wait");
            return true;
        } catch (Exception e) {
            Log.info("Expected element is not displayed on screen hence timeout");
            return false;
        }
    }

    public boolean isElementPresent(WebElement element, String time) {
        if (element == null) {
            return false;
        }
        int timeoutInSeconds;
        try {
            timeoutInSeconds = Integer.parseInt(time);
        } catch (NumberFormatException e) {
            Log.info("Invalid timeout value: " + time);
            return false;
        }
        try {
            org.openqa.selenium.support.ui.Wait<WebElement> wait = new FluentWait<>(element)
                    .withTimeout(Duration.ofSeconds(timeoutInSeconds))
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class);
            wait.until(el -> {
                try {
                    return el.isDisplayed();
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            });
            Log.info("Element is present on screen");
            return true;
        } catch (TimeoutException e) {
            Log.info("Element not present within timeout");
            return false;
        } catch (Exception e) {
            Log.info("Exception while checking element presence: " + e.getMessage());
            return false;
        }
    }

    public void hardWait(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            Log.info("Hard wait completed for " + seconds + " seconds", 2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.info("Hard wait interrupted", 2);
        }

}
}