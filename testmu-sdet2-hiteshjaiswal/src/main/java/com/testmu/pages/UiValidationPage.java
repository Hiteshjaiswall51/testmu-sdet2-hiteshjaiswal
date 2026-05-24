package com.testmu.pages;

import com.testmu.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UiValidationPage extends BasePage {

    @FindBy(id = "name")
    private WebElement nameField;

    @FindBy(id = "email")
    private WebElement emailField;

    @FindBy(id = "phone")
    private WebElement phoneField;


    public void fillTheValuesinFiels(String name, String email, String phone){
        this.seleniumHelper.click(nameField);
        this.seleniumHelper.clearAndSetValue(nameField, name);
        this.seleniumHelper.click(emailField);
        this.seleniumHelper.clearAndSetValue(emailField, email);
        this.seleniumHelper.click(phoneField);
        this.seleniumHelper.clearAndSetValue(phoneField, phone);
    }

    public boolean isUserDisplayed(String user){
        return this.seleniumHelper.isElementPresent("//div['"+user+"']'");
    }

}
