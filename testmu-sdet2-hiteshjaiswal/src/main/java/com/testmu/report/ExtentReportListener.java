package com.testmu.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Category;
import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.testmu.utils.ReportUtility;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.util.*;

public class ExtentReportListener implements IReporter {
    private static final String OUTPUT_FOLDER = "reports/extent";
    private static final String FILE_NAME = "/ExtentReport.html";

    private ExtentReports extent;
    private ExtentTest test;
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        init();
        boolean createSuiteNode = false;
        if(suites.size()>1){
            createSuiteNode=true;
        }
        for (ISuite suite : suites) {
            Map<String, ISuiteResult> result = suite.getResults();

            if(result.size()==0){
                continue;
            }

            int suiteFailSize=0;
            int suitePassSize=0;
            int suiteSkipSize=0;
            ExtentTest suiteTest=null;
            if(createSuiteNode){
                suiteTest = extent.createTest(suite.getName()).assignCategory(suite.getName());
            }
            boolean createSuiteResultNode = false;
            if(result.size()>1){
                createSuiteResultNode=true;
            }
            Date suiteStartTime = null,suiteEndTime=new Date();
            for (ISuiteResult r : result.values()) {
                ExtentTest resultNode;
                ITestContext context = r.getTestContext();
                if(createSuiteResultNode){

                    if( null == suiteTest){
                        resultNode = extent.createTest(context.getName());
                    }else{
                        resultNode = suiteTest.createNode(context.getName());
                    }
                }else{
                    resultNode = suiteTest;
                }
                if(resultNode != null){
                    resultNode.assignCategory(suite.getName(),context.getName());
                    if(suiteStartTime == null){
                        suiteStartTime = context.getStartDate();
                    }
                    suiteEndTime = context.getEndDate();
                    resultNode.getModel().setStartTime(context.getStartDate());
                    resultNode.getModel().setEndTime(context.getEndDate());

                    int passSize = context.getPassedTests().size();
                    int failSize = context.getFailedTests().size();
                    int skipSize = context.getSkippedTests().size();
                    suitePassSize += passSize;
                    suiteFailSize += failSize;
                    suiteSkipSize += skipSize;
                    if(failSize>0){
                        resultNode.getModel().setStatus(Status.FAIL);
                    }
                    resultNode.getModel().setDescription(String.format("Pass: %s ; Fail: %s ; Skip: %s ;",passSize,failSize,skipSize));
                }
                buildTestNodes(resultNode,context.getFailedTests(), Status.FAIL);
                buildTestNodes(resultNode,context.getSkippedTests(), Status.SKIP);
                buildTestNodes(resultNode,context.getPassedTests(), Status.PASS);
            }
            if(suiteTest!= null){
                suiteTest.getModel().setDescription(String.format("Pass: %s ; Fail: %s ; Skip: %s ;",suitePassSize,suiteFailSize,suiteSkipSize));
                suiteTest.getModel().setStartTime(suiteStartTime==null?new Date():suiteStartTime);
                suiteTest.getModel().setEndTime(suiteEndTime);
                if(suiteFailSize>0){
                    suiteTest.getModel().setStatus(Status.FAIL);
                }
            }

        }
        extent.flush();

    }

    private void init() {

        File reportDir= new File(OUTPUT_FOLDER);
        if(!reportDir.exists()&& !reportDir .isDirectory()){
            reportDir.mkdirs();
        }
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(OUTPUT_FOLDER+ FILE_NAME);
        htmlReporter.config().setDocumentTitle(ReportUtility.getReportName());
        htmlReporter.config().setReportName(ReportUtility.getReportName());
        htmlReporter.config().setTheme(Theme.STANDARD);
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setReportUsesManualConfiguration(true);
        Properties properties = System.getProperties();
        extent.setSystemInfo("os.name",properties.getProperty("os.name","==="));
        extent.setSystemInfo("os.arch",properties.getProperty("os.arch","==="));
        extent.setSystemInfo("os.version",properties.getProperty("os.version","==="));
        extent.setSystemInfo("java.version",properties.getProperty("java.version","==="));
        extent.setSystemInfo("java.home",properties.getProperty("java.home","==="));
        extent.setSystemInfo("user.name",properties.getProperty("user.name","==="));
        extent.setSystemInfo("user.dir",properties.getProperty("user.dir","==="));
    }

    private void buildTestNodes(ExtentTest extenttest, IResultMap tests, Status status) {

        String[] categories=new String[0];
        if(extenttest != null ){
            Set<Category> categoryList = extenttest.getModel().getCategorySet();
            categories = new String[categoryList.size()];
            for(int index=0;index<categoryList.size();index++){
                categories[index] = categoryList.getClass().getName();
            }
        }


        if (tests.size() > 0) {
            Set<ITestResult> treeSet = new TreeSet<ITestResult>(new Comparator<ITestResult>() {
                @Override
                public int compare(ITestResult o1, ITestResult o2) {
                    return o1.getStartMillis()<o2.getStartMillis()?-1:1;
                }
            });
            treeSet.addAll(tests.getAllResults());
            for (ITestResult result : treeSet) {
                Object[] parameters = result.getParameters();
                String name="";
                for(Object param:parameters){
                    name+=param.toString();
                }
                if(name.length()>0){
                    if(name.length()>50){
                        name= name.substring(0,49)+"...";
                    }
                }else{
                    name = result.getMethod().getMethodName();
                }
                if(extenttest==null){
                    test = extent.createTest(name);
                }else{
                    test = extenttest.createNode(name).assignCategory(categories);
                }
                //test.getModel().setDescription(description.toString());
                //test = extent.createTest(result.getMethod().getMethodName());
                for (String group : result.getMethod().getGroups())
                    test.assignCategory(group);

                List<String> outputList = Reporter.getOutput(result);
                try {
                    for(String output:outputList){
                        //String screenshotPath= "C:\\Users\\saksh\\Desktop\\regression3.2\\Reports\\14May_133039\\CreateEnvironment_ConfigMap_FileUpload_KeyValue_chrome\\CreateEnvironment_ConfigMap_FileUpload_KeyValue_chrome_135_May_133042.jpg";

                        if(output.contains("<img") && output.contains("src='")) {
                            String screenshotPath = extractScreenshotPath(output);
                            test.info("Failure screenshot",
                                    MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                        }
                        else {
                            test.info(cleanReportOutput(output).replaceAll("<","&lt;").replaceAll(">","&gt;"));
                        }

                    }
                    if (result.getThrowable() != null) {
                        // String screenshotPath= "C:\\Users\\saksh\\Desktop\\regression3.2\\Reports\\14May_133039\\CreateEnvironment_ConfigMap_FileUpload_KeyValue_chrome\\CreateEnvironment_ConfigMap_FileUpload_KeyValue_chrome_135_May_133042.jpg";

                        test.log(status, result.getThrowable());
                    }
                    else {
                        // String screenshotPath= "C:\\Users\\saksh\\Desktop\\regression3.2\\Reports\\14May_133039\\CreateEnvironment_ConfigMap_FileUpload_KeyValue_chrome\\CreateEnvironment_ConfigMap_FileUpload_KeyValue_chrome_135_May_133042.jpg";

                        test.log(status, "Test " + status.toString().toLowerCase() + "ed");
                    }
                }
                catch(Exception e)
                {
                    test.log(status, e.getMessage());
                }

                Iterator logIterator = test.getModel().getLogs().iterator();
                while (logIterator.hasNext()){
                    Log log = (Log) logIterator.next();
                    String details = log.getDetails();
                    if(details.contains(ReportUtility.getSpiltTimeAndMsg())){
                        String time = details.split(ReportUtility.getSpiltTimeAndMsg())[0];
                        log.setTimestamp(getTime(Long.valueOf(time)));
                        log.setDetails(details.substring(time.length()+ReportUtility.getSpiltTimeAndMsg().length()));
                    }else{
                        log.setTimestamp(getTime(result.getEndMillis()));
                    }
                }
                test.getModel().setStartTime(getTime(result.getStartMillis()));
                test.getModel().setEndTime(getTime(result.getEndMillis()));
            }
        }
    }


    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    private String extractScreenshotPath(String output) {
        int start = output.indexOf("src='") + 5;
        int end = output.indexOf("'", start);
        if (start < 5 || end <= start) {
            return output;
        }
        return output.substring(start, end);
    }

    private String cleanReportOutput(String output) {
        String splitToken = ReportUtility.getSpiltTimeAndMsg();
        if (output != null && output.contains(splitToken)) {
            return output.substring(output.indexOf(splitToken) + splitToken.length());
        }
        return output;
    }
}
