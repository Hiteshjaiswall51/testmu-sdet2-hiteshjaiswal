package com.testmu.utils;

import com.testmu.helper.SeleniumHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Reporter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static Logger Log = LogManager.getLogger(Configuration.class.getName());
    static Properties prop = new Properties();
    static {

        try {
            loadProperties();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void loadProperties() throws IOException {
        // TODO Auto-generated method stub
        InputStream common=Configuration.class.getResourceAsStream("common.properties");

        if(common!=null)
        {
            prop.load(common);
            common.close();
        }

        InputStream projectproperties=Configuration.class.getClassLoader().getResourceAsStream("project.properties");

        if(projectproperties!=null)
        {
            prop.load(projectproperties);
            projectproperties.close();
        }

        prop.put("executionid",TimeUtility.currentDateAndTime("ddMMM_HHmmss"));
        prop.put("userDir",getSystemVariable("user.dir"));
        //prop.put("user1",getEnvironmentVariable("user1.username"));
        //prop.put("password1",getEnvironmentVariable("user1.password"));
        Log.info("Test Execution ID : "+Configuration.get("executionid"));
    }

    public static String get(String key) {
        //1. load data from properties file
        String value=	prop.getProperty(key, "NA");
        if(StringUtils.isEmpty(value) || "NA".equalsIgnoreCase(value)) {
            Log.error("Value is not specified for key: "+key + " in properties file.");
            return null;
        }
        return value;
    }

    public static String getOptional(String key) {
        String value = prop.getProperty(key);
        if (StringUtils.isEmpty(value) || "NA".equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }

    public static String getSystemVariable(String key) {
        //1. load data from properties file

        String value=System.getProperty(key);
        if(!value.isEmpty()) {
            try {

                Reporter.log(value, 0, true);
                throw new Exception("Value is not specified for key: "+key + " in system.");
            }catch(Exception e) {}
        }
        return value;


    }

    public static String getEnvironmentVariable(String key) {
        //1. load data from properties file
        String value=System.getenv(key);
        if(!value.isEmpty())
        {
            Reporter.log("Unable to find key value in environment Variables, checking in Properties file : "+key, 0, true);
            value=Configuration.get(key);
        }
        if(!value.isEmpty()) {
            try {
                Reporter.log("Value is not specified for key: "+key + " in system.", 0, true);
                throw new Exception("Value is not specified for key: "+key + " in system.");
            }catch(Exception e) {}
        }
        Reporter.log("Value found for key: "+key, 0, true);
        return value;


    }
}
