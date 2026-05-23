package com.testmu.base;

import com.aventstack.extentreports.ExtentReports;
import com.testmu.helper.*;
import com.testmu.utils.FrameworkConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.HashMap;

public class BaseTest {
    protected final FrameworkConfig config = ConfigFactory.create(FrameworkConfig.class);
    private static Logger Log = LogManager.getLogger(BaseTest.class.getName());
    protected final Wait waitHelper = new Wait();
    protected final PageHolder pageHolder = new PageHolder();
    protected final JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor(waitHelper);
    protected final MouseActions mouseAction = new MouseActions(waitHelper, javaScriptExecutor);
    protected final DropDown dropdown = new DropDown(waitHelper);
    protected final Frame frames = new Frame();
    protected final SeleniumHelper seleniumHelper = new SeleniumHelper(waitHelper, javaScriptExecutor);
    protected final Assertion assertion = new Assertion();
    public static ExtentReports extent = null;
    public static RequestSpecification requestSpecificationstatic = null;
    public static TestCase _session;
    public Response response = null;

    @BeforeTest
    public void reportSetup()  {
    }

    @BeforeMethod(alwaysRun = true)
    public final void setUpBaseTest(ITestContext test, Method method)
            throws Exception {
        _session = new TestCase(method.getName(), method.getDeclaringClass().getPackage().getName());
        seleniumHelper.setDriver(_session);
        beforeEachTest(method);
    }

    @AfterMethod(alwaysRun = true)
    public final void tearDownBaseTest(ITestResult result) throws Exception {
        try {
            afterEachTest(result);
        } finally {
            Reporter.setCurrentTestResult(result);
            System.out.println("Test Descpription: " + result.getMethod().getDescription());
            Log.info("####### End of Test Case: " + _session.get_testCaseName() + " #######");
            seleniumHelper.closeDriver();
        }
    }

    protected void beforeEachTest(Method method) throws Exception {
        if (shouldLoginBeforeEachTest()) {
            loginBeforeEachTest();
        }
    }

    protected void afterEachTest(ITestResult result) throws Exception {
    }

    protected boolean shouldLoginBeforeEachTest() {
        return true;
    }

    protected void loginBeforeEachTest() throws Exception {
//        new LoginPage().login(config.username(), config.password());
    }

    @BeforeSuite
    protected void setUp(ITestContext test) {
    }

    @AfterSuite
    protected void setDown(ITestContext test) {
    }

    @AfterTest
    public void tearDown() {
    }
}
