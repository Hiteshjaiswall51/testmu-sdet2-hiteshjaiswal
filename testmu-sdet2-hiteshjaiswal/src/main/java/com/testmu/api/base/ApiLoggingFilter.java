package com.testmu.api.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testmu.utils.ReportUtility;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class ApiLoggingFilter implements Filter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext context) {
        ReportUtility.log("API Base URI: " + requestSpec.getBaseUri());
        ReportUtility.log("API Request: " + requestSpec.getMethod() + " " + requestSpec.getURI());

        Object requestBody = requestSpec.getBody();
        if (requestBody != null) {
            ReportUtility.log("API Request Body:\n" + prettyPrint(requestBody));
        }

        long startedAt = System.currentTimeMillis();
        try {
            Response response = context.next(requestSpec, responseSpec);
            long responseTime = System.currentTimeMillis() - startedAt;

            ReportUtility.log("API Response Status: " + response.getStatusCode() + " " + response.getStatusLine());
            ReportUtility.log("API Response Time: " + responseTime + " ms");
            ReportUtility.log("API Response Body:\n" + responseBody(response));

            return response;
        } catch (RuntimeException e) {
            long responseTime = System.currentTimeMillis() - startedAt;
            ReportUtility.log("API Request Failed After: " + responseTime + " ms");
            ReportUtility.log("API Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw e;
        }
    }

    private String responseBody(Response response) {
        try {
            return response.asPrettyString();
        } catch (Exception e) {
            return response.asString();
        }
    }

    private String prettyPrint(Object value) {
        try {
            String rawJson = value instanceof String ? (String) value : MAPPER.writeValueAsString(value);
            Object json = MAPPER.readValue(rawJson, Object.class);
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }
}
