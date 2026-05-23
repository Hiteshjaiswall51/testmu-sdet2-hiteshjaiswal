package com.testmu.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MouseActions {
    private final PageHolder pageHolder = new PageHolder();
    private final Wait waitHelper;
    private final JavaScriptExecutor javaScriptExecutor;
    private static Logger Log = LogManager.getLogger(MouseActions.class.getName());
    public MouseActions(Wait waitHelper, JavaScriptExecutor javaScriptExecutor) {
        this.waitHelper = waitHelper;
        this.javaScriptExecutor = javaScriptExecutor;
    }

    public void mouseClick(WebElement element) {
        new Actions(pageHolder.getDriver()).click(element).build().perform();
        Log.info("Clicked element using mouse action", 2);
    }

    public void mouseClick(WebElement element, String fieldName) {
        mouseClick(element);
    }

    public void mouseMoveAndClick(WebElement element) {
        new Actions(pageHolder.getDriver()).moveToElement(element).click(element).build().perform();
        Log.info("Moved to element and clicked using mouse action", 2);
    }

    public void mouseMoveAndClick(WebElement element, String fieldName) {
        mouseMoveAndClick(element);
    }

    public void mouseMoveToElement(WebElement element) {
        javaScriptExecutor.executeJavascript("arguments[0].scrollIntoView();", element);
        Log.info("Moved to element", 2);
    }

    public void mouseMoveToElement(WebElement element, String fieldName) {
        mouseMoveToElement(element);
    }

    public void mouseClickHoldAndDrop(WebElement sourceElement, WebElement targetElement) {
        Actions builder = new Actions(pageHolder.getDriver());
        Action dragAndDrop = builder.clickAndHold(sourceElement).moveToElement(targetElement).release(targetElement).build();
        waitHelper.hardWait(2);
        dragAndDrop.perform();
        Log.info("Performed drag and drop action", 2);
    }

    public String mouseHoverAndGetText(WebElement element) {
        try {
            String hoverText = element.getAttribute("title");
            Log.info("Fetched hover text", 2);
            return hoverText;
        } catch (Exception e) {
            Log.error(" Element :" + element + " could not get the text on mouse hover");
            return null;
        }
    }

    public void mouseHover(WebElement element) {
        try {
            new Actions(pageHolder.getDriver()).moveToElement(element).perform();
            Log.info("Hovered over element", 2);
        } catch (Exception e) {
            Log.error(" Failed to mouse hover on element");
        }
    }

    public void mouseHoverByXpath(String xpath) {
        WebDriver driver = pageHolder.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            wait.until(ExpectedConditions.elementToBeClickable(element));
            javaScriptExecutor.executeJavascript(
                    "arguments[0].scrollIntoView({block:'center', inline:'nearest'});",
                    element
            );
            new Actions(driver).moveToElement(element).pause(Duration.ofMillis(300)).perform();
            wait.until(ExpectedConditions.visibilityOf(element));
            Log.info("Hovered over element using xpath: " + xpath, 2);
        } catch (Exception e) {
            Log.error("Failed to mouse hover on element with xpath: ");
            throw e;
        }
    }

    public void keyPress(String key) {
        Actions actions = new Actions(pageHolder.getDriver());
        try {
            Keys seleniumKey = Keys.valueOf(key.toUpperCase());
            actions.sendKeys(seleniumKey).perform();
            Log.info("Pressed key : " + key, 2);
        } catch (IllegalArgumentException e) {
            actions.sendKeys(key).perform();
            Log.info("Pressed normal text : " + key, 2);
        }
    }

    public void keyPress(CharSequence key) {
        Actions actions = new Actions(pageHolder.getDriver());
        actions.sendKeys(key).perform();
        Log.info("Pressed key " + key, 2);
    }

    public void sendkeysWithActionsClass(WebElement element, String value) {
        try {
            new Actions(pageHolder.getDriver()).moveToElement(element).click().sendKeys(value).build().perform();
            Log.info("Sent keys using actions class", 2);
        } catch (Exception e) {
            Log.info(" Element :" + element + " could not perform sendkeys using actions class", 2);
        }
    }
}
