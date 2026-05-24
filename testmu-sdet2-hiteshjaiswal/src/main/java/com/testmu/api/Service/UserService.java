package com.testmu.api.Service;

import com.testmu.api.model.UserModel;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserService {
    public Response createUser(UserModel payload){
        return given()
                .contentType("application/json")
                .body(payload)
                .post("/users");

    }

}
