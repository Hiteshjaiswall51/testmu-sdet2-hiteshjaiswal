package com.testmu.helper;

import com.testmu.utils.Pause;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

public class JavaScriptExecutor {
    private final PageHolder pageHolder = new PageHolder();
    private final Wait waitHelper;
    private static Logger Log = LogManager.getLogger(JavaScriptExecutor.class.getName());
    public JavaScriptExecutor(Wait waitHelper) {
        this.waitHelper = waitHelper;
    }

    public void executeJavascript(String script) {
        Reporter.log("Executing javascript: " + script);
        Log.info("Executing javascript", 2);
        ((org.openqa.selenium.JavascriptExecutor) pageHolder.getDriver()).executeScript(script);
    }

    public Object executeJavascript(String script, Object... arguments) {
        Log.info("Executing javascript with arguments", 2);
        return ((org.openqa.selenium.JavascriptExecutor) pageHolder.getDriver()).executeScript(script, arguments);
    }

    public void javaScriptClick(WebElement element) {
        try {
            scrollToElement(element);
            ((org.openqa.selenium.JavascriptExecutor) pageHolder.getDriver()).executeScript("arguments[0].click();", element);
            Log.info("Clicked element through javascript", 2);
        } catch (Exception e) {
            Reporter.log(" Element :" + element + " is not clickable using javaScript", 0, true);
            Log.error("Element :" + element + " is not clickable using javaScript");
        }
    }

    public void javaScriptClick(WebElement element, String fieldName) {
        javaScriptClick(element);
    }

    public void javaScriptGetTextFromMonacoEditor() {
        try {
            String value = (String) ((org.openqa.selenium.JavascriptExecutor) pageHolder.getDriver())
                    .executeScript("return monaco.editor.getModels()[0].getValue();");
            System.out.println(value);
            Log.info("Fetched Monaco editor text", 2);
        } catch (Exception e) {
            Reporter.log("Unable to fetch text of Monaco editor", 0, true);
            Log.error("Unable to fetch text of Monaco editor");
        }
    }

    public WebElement scrollToElement(WebElement element) {
        try {
            ((org.openqa.selenium.JavascriptExecutor) pageHolder.getDriver()).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'instant', block: 'center', inline: 'center'});",
                    element
            );
            Thread.sleep(300);
            Log.info("Scrolled to " +element + "element", 2);
            return element;
        } catch (Exception e) {
            Reporter.log("Exception while scrolling to element: " + e.getMessage(), 0, false);
            Log.error("Exception while scrolling to element: " + e.getMessage());
            return null;
        }
    }

    public void scrollTo(int xpos, int ypos) {
        Log.info("Scrolling to coordinates x:" + xpos + " y:" + ypos, 2);
        executeJavascript("window.scrollTo(" + xpos + "," + ypos + ")");
    }

    public void clearUsingJavaScript(WebElement element) {
        WebElement visibleElement = waitHelper.waitForElementToDisplay(element, Long.parseLong(Pause.MEDIUM));
        visibleElement.sendKeys(Keys.chord(Keys.CONTROL + "a", Keys.DELETE));
        Log.info("Cleared element using keyboard shortcut", 2);
    }

    public void clearUsingJavaScript(WebElement element, String value) {
        clearUsingJavaScript(element);
    }

    public void sendkeysWithJavascript(WebElement element, String value) {
        try {
            ((org.openqa.selenium.JavascriptExecutor) pageHolder.getDriver())
                    .executeScript(
                            "const input = arguments[0];" +
                                    "const nextValue = arguments[1];" +
                                    "input.focus();" +
                                    "const prototype = Object.getPrototypeOf(input);" +
                                    "const descriptor = Object.getOwnPropertyDescriptor(prototype, 'value');" +
                                    "if (descriptor && descriptor.set) {" +
                                    "  descriptor.set.call(input, nextValue);" +
                                    "} else {" +
                                    "  input.value = nextValue;" +
                                    "}" +
                                    "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                                    "input.dispatchEvent(new Event('change', { bubbles: true }));" +
                                    "input.dispatchEvent(new Event('blur', { bubbles: true }));",
                            element,
                            value
                    );
            Log.info("Set value using javascript", 2);
        } catch (Exception e) {
            Log.info(" Element :" + element + " could not perform sendkeys using javascript", 2);
        }
    }
}
