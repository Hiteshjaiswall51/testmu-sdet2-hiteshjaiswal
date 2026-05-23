package com.testmu.helper;

import com.testmu.utils.Pause;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Frame {
    private final PageHolder pageHolder = new PageHolder();
    public void switchToFrame(WebElement frame) {
        WebDriverWait wait = new WebDriverWait(pageHolder.getDriver(), Pause.duration(Pause.MEDIUM));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame));
    }
}
