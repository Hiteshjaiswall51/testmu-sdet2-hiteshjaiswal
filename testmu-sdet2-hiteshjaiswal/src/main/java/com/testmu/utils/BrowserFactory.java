package com.testmu.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;


public class BrowserFactory {
    FrameworkConfig config = ConfigFactory.create(FrameworkConfig.class);

    private boolean isHeadlessExecution() {
        String headless = System.getProperty("headless", config.headless());
        return "yes".equalsIgnoreCase(headless) || "true".equalsIgnoreCase(headless);
    }

    public WebDriver getBrowserInstance(String browser) throws Exception {
        RemoteWebDriver driver= null;
        try {
//            String remoteUrl = System.getProperty("remote_url");
            String remoteUrl = "http://localhost:4444/wd/hub";
            if(browser.equalsIgnoreCase("chrome")){
                ChromeOptions options = new ChromeOptions();
                if (isHeadlessExecution()) {
                    options.addArguments("--headless=new");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--remote-allow-origins=*");
                }
                if (remoteUrl != null && !remoteUrl.isEmpty()) {
                    driver = new RemoteWebDriver(new URL(remoteUrl), options);
                } else {
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver(options);
                }
            } else if (browser.equalsIgnoreCase("fire-fox")) {
//                ChromeOptions options = new ChromeOptions();
                FirefoxOptions options = new FirefoxOptions();
                if (isHeadlessExecution()) {
                    options.addArguments("--headless=new");
                }
                if (remoteUrl != null && !remoteUrl.isEmpty()) {
                    driver = new RemoteWebDriver(new URL(remoteUrl), options);
                } else {
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver(options);
                }
            }
        }catch (Exception e){
            throw new Exception("Exception in getting browser instance" + e);
        }
        return driver;
    }

}
