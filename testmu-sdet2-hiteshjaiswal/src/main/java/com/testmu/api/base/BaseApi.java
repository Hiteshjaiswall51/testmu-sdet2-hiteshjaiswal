package com.testmu.api.base;

import com.testmu.utils.FrameworkConfig;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.testng.annotations.BeforeClass;

public class BaseApi {
    protected final FrameworkConfig config = ConfigFactory.create(FrameworkConfig.class);
    @BeforeClass
    public void setupApi() {
        RestAssured.baseURI = config.uri();
    }

}
