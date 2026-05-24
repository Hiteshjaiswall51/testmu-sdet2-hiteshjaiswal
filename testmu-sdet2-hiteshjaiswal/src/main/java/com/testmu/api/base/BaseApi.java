package com.testmu.api.base;

import com.testmu.report.ExtentReportListener;
import com.testmu.utils.FrameworkConfig;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

@Listeners(ExtentReportListener.class)
public class BaseApi {
    protected final FrameworkConfig config = ConfigFactory.create(FrameworkConfig.class);
    @BeforeClass
    public void setupApi() {
        RestAssured.reset();
        RestAssured.baseURI = config.uri();
        RestAssured.filters(new ApiLoggingFilter());
    }

}
