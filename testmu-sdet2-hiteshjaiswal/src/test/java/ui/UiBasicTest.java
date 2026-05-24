package ui;

import com.testmu.base.BaseTest;
import com.testmu.base.TestCase;
import com.testmu.constant.UiValConstant;
import com.testmu.listner.RetryCountIfFailed;
import com.testmu.pages.UiValidationPage;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class UiBasicTest extends BaseTest {


    @BeforeTest
    void openUrl() throws Exception {
        String url=config.url();
        this.seleniumHelper.launch(url);
    }

    @Test(groups={"basic-test"}, priority=0)
    @RetryCountIfFailed(2)
    void basicTestUi(){
    UiValidationPage uival=new UiValidationPage();
    uival.fillTheValuesinFiels(UiValConstant.name, UiValConstant.email, UiValConstant.phone);
    }
}
