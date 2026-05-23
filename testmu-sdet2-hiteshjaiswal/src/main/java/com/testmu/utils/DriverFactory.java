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
        if(getInstance() == null){
            return false;
        }
        return true;
    }
    public void setDriver(WebDriver driverParm) {
        driver.set(driverParm);
    }
    public void closeBrowser() {
            driver.get().quit();
            driver.remove();
    }

}
