package com.testmu.helper;

import com.testmu.base.TestCase;
import com.testmu.utils.BrowserFactory;
import com.testmu.utils.DriverFactory;
import com.testmu.utils.FrameworkConfig;
import com.testmu.utils.Pause;
import com.testmu.utils.ReportUtility;
import junit.framework.Assert;
import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
            report("Clicked on " + getCleanLocator(element));
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
    String locator = getCleanLocator(element);
    report("Waiting to click on " + locator);
    try {
        WebElement visibleElement = waitUntilClickable(element, Pause.SMALL);
        javaScriptExecutor.scrollToElement(visibleElement).click();
        report("Clicked on " + locator);
    } catch (Exception e) {
        WebElement healedElement = selfHealingLocator.healFromElement(element, "click");
        if (healedElement != null) {
            try {
                WebElement visibleElement = waitUntilClickable(healedElement, Pause.SMALL);
                javaScriptExecutor.scrollToElement(visibleElement).click();
                report("Clicked self-healed element for " + locator);
                return;
            } catch (Exception clickException) {
                report("Self-healed click failed for " + locator + " | " + clickException.getMessage());
            }
        }
        String message = "Unable to click on " + locator + " | " + e.getMessage();
        report(message);
        Assert.fail(message);
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
    try {
        if (pageHolder.getDriver() == null) {
            return null;
        }
        String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        TakesScreenshot ts = (TakesScreenshot) pageHolder.getDriver();
        String fileName = "screenshot_" + dateName + ".png";
        File screenshotDirectory = new File(System.getProperty("user.dir"), "reports/extent/screenshots");
        if (!screenshotDirectory.exists()) {
            screenshotDirectory.mkdirs();
        }
        File source = ts.getScreenshotAs(OutputType.FILE);
        File finalDestination = new File(screenshotDirectory, fileName);
        FileHandler.copy(source, finalDestination);
        return "screenshots/" + fileName;
    } catch (Exception e) {
        return null;
    }
}

public boolean isSelected(WebElement element) {
    try {
        return javaScriptExecutor.scrollToElement(element).isSelected();
    } catch (Exception e) {
        Log.info("Unable to fetch the isSelected status of the element", 0, true);
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
            Log.info("Value for Element " + targetName + " is null");
            Assert.fail("Value for Element " + targetName + " is null");
        }

        waitHelper.waitForElementToDisplay(element, Long.parseLong(Pause.MEDIUM));
        waitHelper.waitTillElementIsVisibleAndClickable(element);
        element.clear();
        element.sendKeys(valueToBeSent);
        report("Entered value on " + targetName + " : " + valueToBeSent);
    } catch (Exception e) {
        try {
            waitHelper.waitForElementToDisplay(element, Long.parseLong(Pause.MEDIUM));
            waitHelper.waitTillElementIsVisibleAndClickable(element);
            element.clear();
            element.sendKeys(valueToBeSent);
            report("Entered value after retry on " + targetName);
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
    report("Open URL: " + url);
    setDriver(null);
    pageHolder.getDriver().get(url);
    pageHolder.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
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
        Log.info("element is not present on screen", 2, true);
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
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }
}

public WebElement scrollToElement(WebElement element) {
    return javaScriptExecutor.scrollToElement(element);
}

public void clearAndSetValue(WebElement element, String value) {
    String locator = getCleanLocator(element);
    try {
        WebElement input = waitUntilClickable(element, Pause.SMALL);

        if (value == null) {
            Log.error("Provided string value is null");
            return;
        }

        input.click();

        // Select all + delete
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.DELETE);

        // Fallback if still not cleared
        if (!input.getAttribute("value").isEmpty()) {
            input.clear();
        }

        input.sendKeys(value);
        report("Entered value on " + locator + " : " + value);
    } catch (Exception e) {
        WebElement healedElement = selfHealingLocator.healFromElement(element, "setValue");
        if (healedElement != null) {
            clearAndSetValue(healedElement, value);
            return;
        }
        String message = "Unable to clear and set value on " + locator + " | " + e.getMessage();
        report(message);
        Assert.fail(message);
    }
}

public void clearAndSetValue(String xpath, String value) {
    clearAndSetValue(getElementWithXpath(xpath), value);
}

private void report(String message) {
    Log.info(message);
    ReportUtility.log(message);
}

private WebElement waitUntilClickable(WebElement element, String timeoutSeconds) {
    WebDriverWait wait = new WebDriverWait(pageHolder.getDriver(), Pause.duration(timeoutSeconds));
    return wait.until(ExpectedConditions.elementToBeClickable(element));
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
        Log.info("Fetched value of the object", 2, true);
        return element.getAttribute("value");
    } catch (Exception e) {
        Log.info("Unable to Fetch value of the object", 2, true);
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
        Assert.assertFalse(element + " Not Present", isElementDisplay(element));
    } catch (Exception e) {
        Log.info("Element :" + element + " Not Present");
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
