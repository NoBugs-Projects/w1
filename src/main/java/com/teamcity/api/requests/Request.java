package com.teamcity.api.requests;

import com.teamcity.api.enums.Endpoint;
import io.restassured.specification.RequestSpecification;

/**
 * Abstract base class for all API request implementations.
 * <p>
 * This class provides the foundation for all API request classes in the TeamCity
 * testing framework. It encapsulates common functionality and state required
 * for making HTTP requests to the TeamCity API.
 * </p>
 * 
 * <p>
 * The class follows the principle of making all variables final by default,
 * unless the implementation specifically requires otherwise. Access modifiers
 * are kept private by default and expanded only when necessary to the minimum
 * required level.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see Endpoint
 * @see RequestSpecification
 */
public abstract class Request {

    /* Придерживаемся паттерна, что все переменные по умолчанию final,
    если реализация целенаправленно не требует обратного
    То же самое с модификаторами доступа: по умолчанию private,
    при необходимости расширяем доступ в минимально достаточной мере */
    
    /**
     * The REST Assured request specification used for making HTTP requests.
     * <p>
     * This specification contains authentication, base URI, and other common
     * request configuration that will be applied to all requests made by
     * this request instance.
     * </p>
     */
    protected final RequestSpecification spec;
    
    /**
     * The API endpoint that this request will target.
     * <p>
     * This endpoint defines the specific API resource path that will be used
     * when constructing the full URL for HTTP requests.
     * </p>
     */
    protected final Endpoint endpoint;

    /**
     * Constructs a new Request instance.
     * <p>
     * This constructor initializes the request with the provided specification
     * and endpoint, which are required for all API operations.
     * </p>
     * 
     * @param spec the REST Assured request specification
     * @param endpoint the API endpoint to target
     */
    protected Request(RequestSpecification spec, Endpoint endpoint) {
        this.spec = spec;
        this.endpoint = endpoint;
    }

}
