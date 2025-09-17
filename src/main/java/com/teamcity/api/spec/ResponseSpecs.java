package com.teamcity.api.spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

/**
 * Factory class for creating REST Assured response specifications.
 * <p>
 * This class provides static methods to create pre-configured ResponseSpecification
 * objects for validating HTTP responses in the TeamCity API testing framework.
 * It includes specifications for both successful responses and various error scenarios.
 * </p>
 * 
 * <p>
 * All response specifications are designed to validate both status codes and response
 * body content where applicable, providing comprehensive validation for API responses.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see ResponseSpecification
 */
public final class ResponseSpecs {
    
    /**
     * Private constructor to prevent instantiation.
     */
    private ResponseSpecs() { }

    /**
     * Creates a base ResponseSpecBuilder for building response specifications.
     * <p>
     * This method provides a common starting point for all response specifications,
     * ensuring consistency across different response validation scenarios.
     * </p>
     * 
     * @return a new ResponseSpecBuilder instance
     */
    private static ResponseSpecBuilder defaultResponseBuilder() {
        return new ResponseSpecBuilder();
    }

    // Success responses

    /**
     * Creates a response specification for successful entity creation.
     * <p>
     * This specification validates that the response has a 201 Created status code,
     * which is typically returned when a new entity is successfully created via POST request.
     * </p>
     * 
     * @return a ResponseSpecification for entity creation success
     */
    public static ResponseSpecification entityWasCreated() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    /**
     * Creates a response specification for successful requests.
     * <p>
     * This specification validates that the response has a 200 OK status code,
     * which is typically returned for successful GET, PUT, and PATCH requests.
     * </p>
     * 
     * @return a ResponseSpecification for successful requests
     */
    public static ResponseSpecification requestReturnsOK() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    /**
     * Creates a response specification for successful requests with no content.
     * <p>
     * This specification validates that the response has a 204 No Content status code,
     * which is typically returned for successful DELETE requests or other operations
     * that don't return content.
     * </p>
     * 
     * @return a ResponseSpecification for successful requests with no content
     */
    public static ResponseSpecification requestReturnsNoContent() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_NO_CONTENT)
                .build();
    }

    // Error responses

    /**
     * Creates a response specification for bad request errors.
     * <p>
     * This specification validates that the response has a 400 Bad Request status code,
     * which is typically returned when the request contains invalid data or parameters.
     * </p>
     * 
     * @return a ResponseSpecification for bad request errors
     */
    public static ResponseSpecification requestReturnsBadRequest() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .build();
    }

    /**
     * Creates a response specification for bad request errors with specific error content.
     * <p>
     * This specification validates that the response has a 400 Bad Request status code
     * and contains a specific error key-value pair in the response body.
     * </p>
     * 
     * @param errorKey the error key to validate in the response body
     * @param errorValue the expected error value for the specified key
     * @return a ResponseSpecification for bad request errors with specific content
     */
    public static ResponseSpecification requestReturnsBadRequest(String errorKey, String errorValue) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(errorKey, Matchers.equalTo(errorValue))
                .build();
    }

    /**
     * Creates a response specification for unauthorized errors.
     * <p>
     * This specification validates that the response has a 401 Unauthorized status code,
     * which is typically returned when authentication is required but not provided or invalid.
     * </p>
     * 
     * @return a ResponseSpecification for unauthorized errors
     */
    public static ResponseSpecification requestReturnsUnauthorized() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_UNAUTHORIZED)
                .build();
    }

    /**
     * Creates a response specification for forbidden errors.
     * <p>
     * This specification validates that the response has a 403 Forbidden status code,
     * which is typically returned when the request is valid but the user doesn't have
     * permission to perform the requested action.
     * </p>
     * 
     * @return a ResponseSpecification for forbidden errors
     */
    public static ResponseSpecification requestReturnsForbidden() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .build();
    }

    /**
     * Creates a response specification for forbidden errors with access denied message.
     * <p>
     * This specification validates that the response has a 403 Forbidden status code
     * and contains the "Access denied" message in the response body.
     * </p>
     * 
     * @return a ResponseSpecification for forbidden errors with access denied message
     */
    public static ResponseSpecification requestReturnsForbiddenWithAccessDenied() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .expectBody(Matchers.containsString("Access denied"))
                .build();
    }

    /**
     * Creates a response specification for not found errors.
     * <p>
     * This specification validates that the response has a 404 Not Found status code,
     * which is typically returned when the requested resource does not exist.
     * </p>
     * 
     * @return a ResponseSpecification for not found errors
     */
    public static ResponseSpecification requestReturnsNotFound() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_NOT_FOUND)
                .build();
    }

    /**
     * Creates a response specification for not found errors with entity not found message.
     * <p>
     * This specification validates that the response has a 404 Not Found status code
     * and contains the "Could not find the entity requested" message in the response body.
     * </p>
     * 
     * @return a ResponseSpecification for not found errors with entity not found message
     */
    public static ResponseSpecification requestReturnsNotFoundWithEntityNotFound() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_NOT_FOUND)
                .expectBody(Matchers.containsString("Could not find the entity requested"))
                .build();
    }

    /**
     * Creates a response specification for internal server errors.
     * <p>
     * This specification validates that the response has a 500 Internal Server Error status code,
     * which is typically returned when an unexpected error occurs on the server side.
     * </p>
     * 
     * @return a ResponseSpecification for internal server errors
     */
    public static ResponseSpecification requestReturnsInternalServerError() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .build();
    }

    // Duplicate entity error

    /**
     * Creates a response specification for duplicate entity ID errors.
     * <p>
     * This specification validates that the response has a 400 Bad Request status code
     * and contains a message indicating that the ID is already used by another entity.
     * </p>
     * 
     * @return a ResponseSpecification for duplicate entity ID errors
     */
    public static ResponseSpecification requestReturnsBadRequestWithDuplicateId() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(Matchers.containsString("is already used by another"))
                .build();
    }

    // Duplicate project name error

    /**
     * Creates a response specification for duplicate project name errors.
     * <p>
     * This specification validates that the response has a 400 Bad Request status code
     * and contains a message indicating that a project with the same name already exists.
     * </p>
     * 
     * @return a ResponseSpecification for duplicate project name errors
     */
    public static ResponseSpecification requestReturnsBadRequestWithDuplicateName() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(Matchers.containsString("Project with this name already exists"))
                .build();
    }
}
