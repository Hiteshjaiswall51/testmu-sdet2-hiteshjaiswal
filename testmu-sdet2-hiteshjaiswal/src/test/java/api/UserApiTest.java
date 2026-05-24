package api;

import com.testmu.api.Service.UserService;
import com.testmu.api.base.BaseApi;
import com.testmu.api.model.UserModel;
import com.testmu.helper.Assertion;
import io.restassured.response.Response;
import junit.framework.Assert;
import org.testng.annotations.Test;

public class UserApiTest extends BaseApi {

    @Test
    public void createUserTest(){
        UserModel request = new UserModel();
        UserService userService = new UserService();
        request.setEmail("hiteshjaiswa@gmail.com");
        request.setName("hitesh jaiswa");
        request.setRole("sdet");
        request.setActive("active");
        Response response =
                userService.createUser(request);
        Assert.assertEquals(response.statusCode(), 201);
        System.out.println(response.asPrettyString());
        
    }
}
