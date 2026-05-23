package com.testmu.base;

import com.testmu.helper.*;

public class BasePage {
    protected final Wait waitHelper = new Wait();
    protected final PageHolder pageHolder = new PageHolder();
    protected final JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor(waitHelper);
    protected final MouseActions mouseAction = new MouseActions(waitHelper, javaScriptExecutor);
    protected final DropDown dropdown = new DropDown(waitHelper);
    protected final Frame frames = new Frame();
    protected final SeleniumHelper seleniumHelper = new SeleniumHelper(waitHelper, javaScriptExecutor);
    protected final Assertion assertion = new Assertion();

    protected BasePage() {
        pageHolder.initElements(this);
    }
}
