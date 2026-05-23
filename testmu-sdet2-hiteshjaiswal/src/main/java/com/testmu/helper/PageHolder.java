package com.testmu.helper;

import com.testmu.utils.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class PageHolder {

    private static Logger Log =    LogManager.getLogger(PageHolder.class.getName());

//    just to centralise the calling on driver much cleaner code
    public WebDriver getDriver() {
        return DriverFactory.getInstance().getDriver();
    }

    public boolean isDriverExist() {
        boolean exists = DriverFactory.getInstance().isDriverExist();
        Log.info("Driver Exists");
        return exists;
    }

//    to initilize all the find by we are goin to be using
    public <T> T initElements(T page) {
        PageFactory.initElements(getDriver(), page);
        Log.info("Initialized page elements for " + page.getClass().getSimpleName());
        return page;
    }


}
