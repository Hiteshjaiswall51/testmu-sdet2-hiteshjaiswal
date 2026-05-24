package integration;

import com.testmu.api.Service.UserService;
import com.testmu.api.model.UserModel;
import com.testmu.base.BaseTest;
import com.testmu.helper.Assertion;
import com.testmu.pages.UiValidationPage;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class IntegrationTest extends BaseTest {


    @Test
    public void createUserViaApiAndValidateInUI() {
        UserModel user = new UserModel();
        user.setName("Hitesh Integration");
        user.setEmail("integration@test.com");
        user.setRole("SDET");
        user.setActive(true);

        UserService userService = new UserService();

        Response response =
                userService.createUser(user);

        Assert.assertEquals(response.statusCode(), 201);

        String createdUser =
                response.jsonPath().getString("name");

        UiValidationPage uiPage =
                new UiValidationPage();

        boolean userPresent =
                uiPage.isUserDisplayed(createdUser);
        Assert.assertTrue(userPresent, "user is present on the screen");
    }

}
