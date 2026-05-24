package com.testmu.utils;

import org.openqa.selenium.WebDriver;

public class DriverFactory {

    private DriverFactory(){}

    public static DriverFactory instance = new DriverFactory();

    public static DriverFactory getInstance(){return instance;}

    ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();

    public WebDriver getDriver(){
        return driver.get();
    }
    public boolean isDriverExist(){
        return driver.get() != null;
    }
    public void setDriver(WebDriver driverParm) {
        driver.set(driverParm);
    }
    public void closeBrowser() {
        if (driver.get() == null) {
            return;
        }
            driver.get().quit();
            driver.remove();
    }

}
