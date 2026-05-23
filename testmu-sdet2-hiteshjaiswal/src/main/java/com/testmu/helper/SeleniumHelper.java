package com.testmu.helper;

import com.testmu.base.TestCase;
import com.testmu.utils.BrowserFactory;
import com.testmu.utils.DriverFactory;
import com.testmu.utils.FrameworkConfig;
import com.testmu.utils.Pause;
import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.Reporter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SeleniumHelper{
    FrameworkConfig config = ConfigFactory.create(FrameworkConfig.class);
    private final PageHolder pageHolder = new PageHolder();
    private final Wait waitHelper;
    private final JavaScriptExecutor javaScriptExecutor;
    private final SelfHealingLocator selfHealingLocator = new SelfHealingLocator();
    private static Logger Log = LogManager.getLogger(SeleniumHelper.class.getName());

public SeleniumHelper(Wait waitHelper, JavaScriptExecutor javaScriptExecutor) {
    this.waitHelper = waitHelper;
    this.javaScriptExecutor = javaScriptExecutor;
}

public boolean clickIfPresent(WebElement element) {
    try {
        if (element == null) {
            Log.info("Element is null for: " + getCleanLocator(element), 2);
            return false;
        }

        if (waitHelper.isElementPresent(element, Pause.MEDIUM)) {
            click(element);
            Log.info("Clicked on " + getCleanLocator(element), 2);
            return true;
        }

        Log.info("Element not present: " + getCleanLocator(element), 2);
        return false;
    } catch (StaleElementReferenceException e) {
        Log.info("Stale element while clicking: " + getCleanLocator(element), 2);
        return false;
    } catch (Exception e) {
        Log.info("Unable to click on " + getCleanLocator(element) + " | Reason: " + e.getMessage(), 2);
        return false;
    }
}

public void click(WebElement element) {
    Log.info("Waiting to Click on " + getCleanLocator(element), 2);
    try {
        WebElement visibleElement = waitHelper.waitForElementToDisplay(element, Long.parseLong(Pause.HIGH));
        waitHelper.waitTillElementIsVisibleAndClickable(element);
        javaScriptExecutor.scrollToElement(visibleElement).click();
        Log.info("Clicked on " + getCleanLocator(element), 2);
    } catch (Exception e) {
        try {
            WebElement visibleElement = waitHelper.waitForElementToDisplay(element, Long.parseLong(Pause.HIGH));
            javaScriptExecutor.javaScriptClick(visibleElement);
            Log.info("Clicked through Javascript on :" + getCleanLocator(element), 2);
        } catch (Exception t) {
            WebElement healedElement = selfHealingLocator.healFromElement(element, "click");
            if (healedElement != null) {
                try {
                    javaScriptExecutor.scrollToElement(healedElement).click();
                    Log.info("Clicked self-healed element for " + getCleanLocator(element), 2);
                    return;
                } catch (Exception clickException) {
                    javaScriptExecutor.javaScriptClick(healedElement);
                    Log.info("Clicked self-healed element through Javascript for " + getCleanLocator(element), 2);
                    return;
                }
            }
            System.out.println(t);
            Log.info("Unable to click on " + getCleanLocator(element), 2);
        }
    }
}

public void click(String xpath) {
    try {
        new org.openqa.selenium.support.ui.WebDriverWait(pageHolder.getDriver(), Pause.duration(Pause.MEDIUM))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
    } catch (Exception e) {
        Log.info(String.format("Unable to wait %s", xpath), 2);
    }
    if (isElementPresent(xpath)) {
        click(getElementWithXpath(xpath));
        return;
    }
    WebElement healedElement = selfHealingLocator.healFromXpath(xpath, "click");
    if (healedElement != null) {
        click(healedElement);
    } else {
        Log.info("Unable to click xpath " + xpath + " and no self-healed locator was accepted", 2);
    }
}

public void clickNoWait(WebElement element, String fieldName) {
    try {
        javaScriptExecutor.scrollToElement(element).click();
        Log.info("Clicked on " + getCleanLocator(element), 2);
    } catch (Exception e) {
        try {
            waitHelper.waitForElementToDisplay(element, Long.parseLong(Pause.MEDIUM));
            javaScriptExecutor.javaScriptClick(element);
        } catch (Exception t) {
            WebElement healedElement = selfHealingLocator.healFromElement(element, "clickNoWait");
            if (healedElement != null) {
                javaScriptExecutor.javaScriptClick(healedElement);
                return;
            }
            Log.error(String.format("Unable to click on " + getCleanLocator(element)));
        }
    }
}

public String captureScreenshot() {
    String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
    TakesScreenshot ts = (TakesScreenshot) pageHolder.getDriver();
    String destination = System.getProperty("user.dir") + dateName + ".png";
    try {
        File source = ts.getScreenshotAs(OutputType.FILE);
        File finalDestination = new File(destination);
        FileHandler.copy(source, finalDestination);
        return destination;
    } catch (Exception e) {
        return null;
    }
}

public boolean isSelected(WebElement element) {
    try {
        return javaScriptExecutor.scrollToElement(element).isSelected();
    } catch (Exception e) {
        Reporter.log("Unable to fetch the isSelected status of the element", 0, true);
        return false;
    }
}

public boolean isDriverExist() {
    return pageHolder.isDriverExist();
}

public void clearTextField(WebElement webElement) {
    try {
        click(webElement);
        clearText(webElement);
        Log.info(" Successfully perform Clear text on the Textfield", 2);
    } catch (Exception e) {
        Log.info(" Could not perform Clear text on the Textfield::" + e, 2);
    }
}

public void setValue(WebElement element, String fieldName, String valueToBeSent) {
    String targetName = (fieldName != null && !fieldName.trim().isEmpty()) ? fieldName : getCleanLocator(element);
    try {
        if (valueToBeSent == null) {
            Reporter.log("Value for Element " + targetName + " is null");
            Assert.fail("Value for Element " + targetName + " is null");
        }

        waitHelper.waitForElementToDisplay(element, Long.parseLong(Pause.MEDIUM));
        waitHelper.waitTillElementIsVisibleAndClickable(element);
        element.clear();
        element.sendKeys(valueToBeSent);
        Log.info("Entered value on " + targetName + " : " + valueToBeSent, 2);
    } catch (Exception e) {
        try {
            waitHelper.waitForElementToDisplay(element, Long.parseLong(Pause.MEDIUM));
            waitHelper.waitTillElementIsVisibleAndClickable(element);
            element.clear();
            element.sendKeys(valueToBeSent);
            Log.info("Entered value after retry on " + targetName, 2);
        } catch (Exception t) {
            WebElement healedElement = selfHealingLocator.healFromElement(element, "setValue");
            if (healedElement != null) {
                setValue(healedElement, targetName, valueToBeSent);
                return;
            }
            System.out.println(t);
            Log.info("Unable to set value on " + targetName, 2);
            Assert.fail("Unable to set value on " + targetName + " EXCEPTION DETAILS: " + t);
        }
    }
}

public void fileUpload(String browserName, String fileUploadPath) {
    String exeFilePath = System.getProperty("user.dir") + "/src/test/resources/FileUpload1.exe";
    try {
        Runtime.getRuntime().exec(exeFilePath + " " + browserName + " " + fileUploadPath);
    } catch (Exception ignored) {
    }
}

public String getTextUsingXpath(String xpath) {
    WebElement element = getElementWithXpath(xpath);
    return getText(element);
}

public void compareText(String actual, String expected) {
    try {
        if (actual.equals(expected)) {
            Log.info("Validated successfully value as " + actual);
        } else {
            Log.error("Unable to Validate  value , expected:" + expected + "; found:" + actual);
        }
    } catch (Exception e) {
        Log.error("unable to validate text " + e);
    }
}

public void setDriver(TestCase session) throws Exception {
    if (!hasDriverQuit()) {
        return;
    }
    BrowserFactory browserFactory = new BrowserFactory();
    DriverFactory.getInstance().setDriver(browserFactory.getBrowserInstance(config.browser()));
    pageHolder.getDriver().manage().window().maximize();
    if (session != null) {
        session.setDriver(pageHolder.getDriver());
    }
}

public boolean hasDriverQuit() {
    try {
        String title = pageHolder.getDriver().getTitle();
        if (title != null && !title.trim().isEmpty()) {
            return false;
        }

        boolean flag = ((RemoteWebDriver) pageHolder.getDriver()).getSessionId() == null;
        if (flag && pageHolder.getDriver() != null) {
            pageHolder.getDriver().close();
        }
        return flag;
    } catch (Exception e) {
        return true;
    }
}

public void launch(String url) throws Exception {
    Log.info("Open URL:" + url, 2);
    setDriver(null);
    pageHolder.getDriver().get(url);
    pageHolder.getDriver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
}

public void closeDriver() {
    DriverFactory.getInstance().closeBrowser();
}

public boolean isElementDisplay(WebElement element) {
    try {
        return element.isDisplayed();
    } catch (Exception e) {
        return false;
    }
}

public boolean isElementPresent(String xpath) {
    try {
        return pageHolder.getDriver().findElements(By.xpath(xpath)).size() >= 1;
    } catch (Exception e) {
        Reporter.log("element is not present on screen", 2, true);
        return false;
    }
}

public boolean isElementPresent(WebElement element, String time) {
    return waitHelper.isElementPresent(element, time);
}

public boolean isElementPresent(By locator) {

    WebDriver driver = pageHolder.getDriver();

    try {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

        return !driver.findElements(locator).isEmpty();

    } finally {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }
}

public WebElement scrollToElement(WebElement element) {
    return javaScriptExecutor.scrollToElement(element);
}

public void clearAndSetValue(WebElement element, String value) {
    try {
        waitHelper.waitForElementToDisplay(element, Long.parseLong(Pause.MEDIUM));
        waitHelper.waitTillElementIsVisibleAndClickable(element);

        if (value == null) {
            Log.error("Provided string value is null");
            return;
        }

        element.click();

        // Select all + delete
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.DELETE);

        // Fallback if still not cleared
        if (!element.getAttribute("value").isEmpty()) {
            element.clear();
        }

        element.sendKeys(value);
    } catch (Exception e) {
        WebElement healedElement = selfHealingLocator.healFromElement(element, "setValue");
        if (healedElement != null) {
            clearAndSetValue(healedElement, value);
            return;
        }
        Log.error("Unable to clear and set value on " + getCleanLocator(element) + " | " + e.getMessage());
        Assert.fail("Unable to clear and set value on " + getCleanLocator(element) + " EXCEPTION DETAILS: " + e);
    }
}

public void clearAndSetValue(String xpath, String value) {
    clearAndSetValue(getElementWithXpath(xpath), value);
}

public void clearSetValueAndPressKey(WebElement element, String value, CharSequence... keys) {
    clearAndSetValue(element, value);
    if (keys != null && keys.length > 0) {
        pressKeys(element, keys);
    } else {
        Log.error("Provided key sequence is null");
    }
}

public void clearSetValueAndPressKey(String xpath, String value, CharSequence... keys) {
    clearSetValueAndPressKey(getElementWithXpath(xpath), value, keys);
}

public void clickClearSetValue(WebElement element, String value) {
    clearAndSetValue(element, value);
}

public void clearAndSetValueWithJavascript(WebElement element, String value) {
    clearText(element);
    if (value != null && value.length() > 0) {
        javaScriptExecutor.sendkeysWithJavascript(element, value);
    }
}

public void pressKeys(WebElement element, CharSequence... keys) {
    try {
        waitHelper.waitForElementToDisplay(element, Long.parseLong(Pause.MEDIUM));
        waitHelper.waitTillElementIsVisibleAndClickable(element);
        element.sendKeys(keys);
        Log.info("Pressed keys on " + getCleanLocator(element), 2);
    } catch (Exception e) {
        try {
            waitHelper.waitForElementToDisplay(element, Long.parseLong(Pause.MEDIUM));
            element.sendKeys(keys);
            Log.info("Pressed keys after retry on " + getCleanLocator(element), 2);
        } catch (Exception t) {
            Log.info("Unable to press keys on " + getCleanLocator(element), 2);
            Assert.fail("Unable to press keys on " + getCleanLocator(element) + " EXCEPTION DETAILS: " + t);
        }
    }
}

public void pressKeys(CharSequence... keys) {
    try {
        WebDriver driver = pageHolder.getDriver();
        driver.switchTo().activeElement().sendKeys(keys);
        Log.info("Pressed keys on active element", 2);
    } catch (Exception e) {
        Log.info("Unable to press keys on active element", 2);
        Assert.fail("Unable to press keys on active element EXCEPTION DETAILS: " + e);
    }
}

public String getText(WebElement element) {
    try {
        return element.getText();
    } catch (Exception e) {
        Log.error("Unable to Fetch text of the object" + e);
        return null;
    }
}

public String getValue(WebElement element) {
    try {
        Reporter.log("Fetched value of the object", 2, true);
        return element.getAttribute("value");
    } catch (Exception e) {
        Reporter.log("Unable to Fetch value of the object", 2, true);
        return null;
    }
}

public void getTextAndValidate(WebElement element, String expectedText, String fieldName) {
    try {
        javaScriptExecutor.scrollToElement(element);
        if (getText(element).equals(expectedText)) {
            Log.info("Validated successfully " + fieldName + " value as " + expectedText);
        } else {
            Log.error("Unable to Validate " + fieldName + " value , expected:" + expectedText + "; found:" + getText(element));
        }
    } catch (Exception e) {
        Log.error("unable to fetchText " + e);
    }
}

public void clearText(WebElement webElement) {
    try {
        waitHelper.waitForElementToDisplay(webElement, Long.parseLong(Pause.MEDIUM));
        waitHelper.waitTillElementIsVisibleAndClickable(webElement);
        webElement.clear();
        Log.info("Cleared text using WebElement.clear()", 2);
    } catch (Exception e) {
        try {
            javaScriptExecutor.clearUsingJavaScript(webElement);
            Log.info("Cleared text using keyboard shortcut fallback", 2);
        } catch (Exception t) {
            Log.info("Unable to clear text on " + getCleanLocator(webElement), 2);
            Assert.fail("Unable to clear text on " + getCleanLocator(webElement) + " EXCEPTION DETAILS: " + t);
        }
    }
}

public void isElementContainsText(WebElement element, String value) {
    try {
        new org.openqa.selenium.support.ui.WebDriverWait(pageHolder.getDriver(), Pause.duration(Pause.MEDIUM))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElement(element, value));
        Log.info("Validated element contains text " + value, 2);
    } catch (Exception e) {
        Log.error("Expected text:" + value + " should be present on screen");
    }
}

public List<String> getTextForElements(String selector) {
    List<WebElement> elements = getElementsWithXpath(selector);
    List<String> textList = new ArrayList<>(elements.size());
    for (WebElement element : elements) {
        textList.add(element.getText());
    }
    return textList;
}

public WebElement getElementWithXpath(String selector) {
    try {
        return pageHolder.getDriver().findElement(By.xpath(selector));
    } catch (org.openqa.selenium.NoSuchElementException e) {
        return selfHealingLocator.healFromXpath(selector, "find");
    }
}

public List<WebElement> getElementsWithXpath(String selector) {
    try {
        return pageHolder.getDriver().findElements(By.xpath(selector));
    } catch (org.openqa.selenium.NoSuchElementException e) {
        return Collections.emptyList();
    }
}

public void validateSubstring(String substring, String string) {
    if (!string.contains(substring)) {
        Assert.fail("Expected '" + substring + "' in '" + string + "'");
    }
}

public boolean validateElementContainsText(String selector, String substring) {
    for (WebElement element : getElementsWithXpath(selector)) {
        if (element.getText().contains(substring)) {
            return true;
        }
    }
    validateSubstring(substring, getElementWithXpath(selector).getText());
    throw new RuntimeException("an earlier check should have thrown an exception");
}

public void validateElementContainsText(WebElement element, String substring) {
    if (getText(element).contains(substring)) {
        Log.info("Successfully validated " + substring);
    } else {
        Log.error("Failed to validate " + substring);
    }
}

public void verifyElementPresent(String xpath, String fieldName) {
    try {
        if (isElementPresent(xpath)) {
            Log.info("Verified " + fieldName + " is present on the screen", 2);
        } else {
            Log.error("Expected " + fieldName + " should be present on the screen");
        }
    } catch (Exception e) {
        Log.error("Unable to Verify " + fieldName);
    }
}

public void verifyElementPresent(WebElement element, String fieldName) {
    try {
        if (waitHelper.isElementPresent(element, Pause.HIGH)) {
            Log.info("Verified " + fieldName + " is present on the screen", 2);
        } else {
            Log.error("Expected " + fieldName + " should be present on the screen");
        }
    } catch (Exception e) {
        Log.error("Unable to Verify " + fieldName);
    }
}

public void verifyElementNotPresent(WebElement element) {
    try {
        waitHelper.waitForElementToDisplay(element, Long.parseLong(Pause.MEDIUM));
        Assert.assertFalse(isElementDisplay(element), element + " Not Present");
    } catch (Exception e) {
        Reporter.log("Element :" + element + " Not Present");
    }
}

public void selectDatePicker(WebElement calenderIcon, WebElement yearDropDown, String month, WebElement rightNextarrow, WebElement selectday) {
    calenderIcon.click();
    while (true) {
        String text = yearDropDown.getText();
        if (text.equals(month)) {
            break;
        }
        rightNextarrow.click();
    }
    selectday.click();
}

public static String getCleanLocator(WebElement element) {
    try {
        String raw = element.toString();

        int index = raw.indexOf("->");
        if (index != -1) {
            String locator = raw.substring(index + 3, raw.length() - 1);

            return locator
                    .replace("xpath: ", "")
                    .replace("css selector: ", "")
                    .replace("data-testid: ", "")
                    .replace("id: ", "");
        }

        return raw;
    } catch (Exception e) {
        return "unknown element";
    }
}
}
