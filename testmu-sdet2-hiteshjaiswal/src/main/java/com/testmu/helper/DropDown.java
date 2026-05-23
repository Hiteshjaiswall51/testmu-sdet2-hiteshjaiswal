package com.testmu.helper;

import com.testmu.utils.Pause;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Reporter;

import java.util.List;

public class DropDown {
    private final PageHolder pageHolder = new PageHolder();
    private final Wait waitHelper;
    private static Logger Log = LogManager.getLogger(DropDown.class.getName());


    public DropDown(Wait waitHelper) {
        this.waitHelper = waitHelper;
    }

    public void selectValueFromDropDown(WebElement inputField, String value) {
        inputField.clear();
        inputField.sendKeys(value);
        waitHelper.hardWait(2);
        WebElement option = pageHolder.getDriver().findElement(By.xpath("//li[contains(text(),'" + value + "')]"));
        option.click();
        Log.info("Selected value from searchable dropdown: " + value, 2);
    }

    public boolean selectValueFromDropDown(String locator, String text) {
        List<WebElement> list = null;
        try {
            WebElement lang = pageHolder.getDriver().findElement(By.xpath(locator));
            list = lang.findElements(By.xpath(locator));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (list == null) {
            Log.info("No dropdown options found for locator: " + locator, 2);
            return false;
        }

        for (WebElement opt : list) {
            String value = opt.getText();
            if (value.equalsIgnoreCase(text)) {
                opt.click();
                Log.info("Selected dropdown option: " + text, 2);
                return true;
            }
        }
        Log.info("Dropdown option not found: " + text, 2);
        return false;
    }

    public void selectValueFromDropDown(WebElement mainSelector, WebElement optionLocator, String text) {
        mainSelector.click();
        waitHelper.isElementPresent(optionLocator, "5");
        waitHelper.hardWait(2);
        optionLocator.findElement(By.xpath("//*[contains(text(),'" + text + "')]")).click();
        Log.info("Selected dropdown option from container: " + text, 2);
    }

    public void selectValueFromDropDownByText(WebElement element, String value) {
        try {
            waitHelper.waitTillElementIsVisibleAndClickable(element);
            Select select = new Select(element);
            select.selectByVisibleText(value);
            Reporter.log("Selected " + value + " from dropdown");
            Log.info("Selected " + value + " from dropdown", 2);
        } catch (Exception e) {
            Log.error("Unable to select element " + value + " from dropdown");
        }
    }

    public void selectValueFromDropDownByIndex(WebElement element, int index) {
        try {
            new Select(element).selectByIndex(index);
            Log.info("Selected element from dropdown at index:" + index, 2);
        } catch (Exception e) {
            Log.error("Unable to select element from dropdown at index :" + index);
        }
    }

    public void selectValueFromDropDownByValue(WebElement element, String value) {
        try {
            new Select(element).selectByValue(value);
            Reporter.log("Selected element " + value + " from dropdown", 2, true);
            Log.info("Selected element " + value + " from dropdown by value", 2);
        } catch (Exception e) {
            Reporter.log("Unable to select element " + value + " from dropdown", 2, true);
            Log.error("Unable to select element " + value + " from dropdown by value");
        }
    }

    public void selectValuefromList(List<WebElement> elements, int value) {
        for (int i = 0; i < value; i++) {
            elements.get(i).click();
            waitHelper.hardWait(10);
        }
        Log.info("Selected " + value + " elements from list", 2);
    }

    public void clickfromList(List<WebElement> elements) {
        for (WebElement element : elements) {
            waitHelper.hardWait(2);
            element.click();
        }
        Log.info("Clicked all elements from list", 2);
    }

    public void clickfromList(List<WebElement> elements, String value) {
        for (WebElement element : elements) {
            waitHelper.hardWait(2);
            if (element.getText().contains(value)) {
                element.click();
                Log.info("Clicked list element containing: " + value, 2);
                break;
            }
        }
    }
}
