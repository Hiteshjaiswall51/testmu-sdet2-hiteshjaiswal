package com.testmu.api.Service;

import com.testmu.api.model.ProductModel;
import com.testmu.api.model.UserModel;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class ProductService {
    public Response addProduct(ProductModel payload){
        return given()
                .contentType("application/json")
                .body(payload)
                .post("/products");
    }
}
