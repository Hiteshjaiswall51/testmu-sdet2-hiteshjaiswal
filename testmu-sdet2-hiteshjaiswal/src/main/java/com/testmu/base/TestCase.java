package com.testmu.base;

import com.testmu.helper.MouseActions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.HashMap;

public class TestCase {
    private static Logger Log = LogManager.getLogger(TestCase.class.getName());
    String _timestamp;
    HashMap<String,String> _data=new HashMap<String,String>();
    HashMap<String,Object> _additionalData=new HashMap<String,Object>();
    String _testCaseName;
    String _testCaseModule;
    WebDriver driver;

    TestCase(String methodName, String packageName){
        Log.info("####### Starting  Test Case: " + methodName +" #######");
        this._testCaseName=methodName;
        this._testCaseModule=packageName;
    }

    public TestCase() {
    }

    public WebDriver getDriver() {
        return driver;
    }
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }
    public String get_timestamp() {
        return _timestamp;
    }
    public void set_timestamp(String _timestamp) {
        this._timestamp = _timestamp;
    }
    public HashMap<String, String> get_data() {
        return _data;
    }
    public void set_data(HashMap<String, String> _data) {
        this._data = _data;
    }

    public void add_dataValue(String key, String value) {
        this._data.put(key, value);
    }

    public String get_dataValue(String key) {
        if((_data==null) || (_data.get(key)==null)) {Log.info("Test Data value not present in file @ correct location for "+key);}
        return this._data.get(key);
    }
    public String get_testCaseName() {
        return _testCaseName==null?"":_testCaseName;
    }
    public void set_testCaseName(String _testCaseName) {
        this._testCaseName = _testCaseName;
    }
    public String get_testCaseModule() {
        return _testCaseModule;
    }
    }
