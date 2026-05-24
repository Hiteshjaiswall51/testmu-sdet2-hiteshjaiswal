package com.testmu.utils;

import org.aeonbits.owner.ConfigFactory;
import org.testng.Reporter;

import java.util.Calendar;

public class ReportUtility {
    private static String reportName = "TestMu_Report";

    private static String splitTimeAndMsg = "<===>";
    public static void log(String msg) {
        long timeMillis = Calendar.getInstance().getTimeInMillis();
        Reporter.log(timeMillis + splitTimeAndMsg + msg, true);
    }

    public static String getReportName() {
        return reportName;
    }

    public static String getSpiltTimeAndMsg() {
        return splitTimeAndMsg;
    }

    public static void setReportName(String reportName) {
        if(!reportName.isEmpty()){
            ReportUtility.reportName = reportName;
        }
    }
}
