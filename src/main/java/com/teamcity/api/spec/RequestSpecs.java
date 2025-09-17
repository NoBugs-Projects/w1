package com.teamcity.api.spec;

import com.github.viclovsky.swagger.coverage.FileSystemOutputWriter;
import com.github.viclovsky.swagger.coverage.SwaggerCoverageRestAssured;
import com.teamcity.api.config.Config;
import com.teamcity.api.models.User;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.nio.file.Paths;
import java.util.List;

import static com.github.viclovsky.swagger.coverage.SwaggerCoverageConstants.OUTPUT_DIRECTORY;

/**
 * Factory class for creating REST Assured request specifications.
 * <p>
 * This class provides static methods to create pre-configured RequestSpecification
 * objects for different authentication scenarios in the TeamCity API testing framework.
 * It includes support for unauthenticated requests, user authentication, super user
 * authentication, and mock server requests.
 * </p>
 * 
 * <p>
 * All request specifications include common filters for logging, Allure reporting,
 * and Swagger coverage analysis to ensure comprehensive test reporting.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see RequestSpecification
 * @see User
 * @see Config
 */
public final class RequestSpecs {

    /**
     * ThreadLocal instance of RequestSpecs for thread safety.
     */
    private static final ThreadLocal<RequestSpecs> SPEC = ThreadLocal.withInitial(RequestSpecs::new);

    /**
     * Private constructor to prevent instantiation.
     */
    private RequestSpecs() {
    }

    /**
     * Creates a request specification for unauthenticated requests.
     * <p>
     * This method creates a RequestSpecification that can be used for API calls
     * that do not require authentication, such as public endpoints or initial
     * server setup requests.
     * </p>
     * 
     * @return a RequestSpecification configured for unauthenticated requests
     */
    public static RequestSpecification unauthSpec() {
        return reqBuilder()
                .setBaseUri("http://" + Config.getProperty("host"))
                .build();
    }

    /**
     * Creates a request specification for user-authenticated requests.
     * <p>
     * This method creates a RequestSpecification that includes HTTP Basic
     * authentication using the provided user's credentials. The authentication
     * is embedded in the URI for easy use with REST Assured.
     * </p>
     * 
     * @param user the user whose credentials should be used for authentication
     * @return a RequestSpecification configured for user-authenticated requests
     * @see User
     */
    public static RequestSpecification authSpec(User user) {
        return reqBuilder()
                .setBaseUri("http://%s:%s@%s".formatted(user.getUsername(), user.getPassword(),
                        Config.getProperty("host")))
                .build();
    }

    /**
     * Creates a request specification for super user-authenticated requests.
     * <p>
     * This method creates a RequestSpecification that uses the super user token
     * for authentication. This is typically used for administrative operations
     * that require elevated privileges.
     * </p>
     * 
     * @return a RequestSpecification configured for super user-authenticated requests
     */
    public static RequestSpecification superUserSpec() {
        return reqBuilder()
                .setBaseUri("http://:%s@%s".formatted(Config.getProperty("superUserToken"), Config.getProperty("host")))
                .build();
    }

    /**
     * Creates a request specification for mock server requests.
     * <p>
     * This method creates a RequestSpecification configured to send requests
     * to a local mock server, typically used for testing without a real
     * TeamCity server instance.
     * </p>
     * 
     * @return a RequestSpecification configured for mock server requests
     */
    public static RequestSpecification mockSpec() {
        return reqBuilder()
                .setBaseUri("http://localhost:8081")
                .build();
    }

    /**
     * Creates a base RequestSpecBuilder with common configuration.
     * <p>
     * This private method creates a RequestSpecBuilder with all the common
     * filters and settings used across all request specifications. It includes
     * filters for request/response logging, Allure reporting, and Swagger
     * coverage analysis.
     * </p>
     * 
     * @return a configured RequestSpecBuilder
     */
    private static RequestSpecBuilder reqBuilder() {
        return new RequestSpecBuilder()
                // Фильтры для отображения реквестов и респонсов в Allure репорте и генерации Swagger Coverage репорта
                .addFilters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured(),
                        new SwaggerCoverageRestAssured(new FileSystemOutputWriter(
                                Paths.get("target/" + OUTPUT_DIRECTORY)))))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON);
    }

}
