package com.teamcity.api.requests;

import com.teamcity.api.enums.Endpoint;
import com.teamcity.api.requests.withoutS.Requester;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;

import java.util.EnumMap;

/**
 * Container class for managing unvalidated API requesters.
 * <p>
 * This class provides a centralized way to access unvalidated requesters for all
 * TeamCity API endpoints. It creates and manages a collection of Requester instances,
 * one for each endpoint defined in the Endpoint enum.
 * </p>
 * 
 * <p>
 * The class uses an EnumMap for efficient endpoint-to-requester mapping and provides
 * a simple interface for retrieving the appropriate requester for any given endpoint.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see Endpoint
 * @see Requester
 * @see RequestSpecification
 */
@Getter
public final class Requesters {

    /**
     * Map of endpoints to their corresponding unvalidated requesters.
     * <p>
     * This EnumMap provides efficient lookup of Requester instances by endpoint.
     * Each endpoint from the Endpoint enum has a corresponding Requester instance
     * that can be used to make unvalidated API calls.
     * </p>
     */
    private final EnumMap<Endpoint, Requester> uncheckedRequests = new EnumMap<>(Endpoint.class);

    /**
     * Constructs a new Requesters instance.
     * <p>
     * This constructor initializes the Requesters by creating a Requester instance
     * for each endpoint defined in the Endpoint enum, all using the provided
     * request specification.
     * </p>
     * 
     * @param spec the REST Assured request specification to use for all requesters
     */
    public Requesters(RequestSpecification spec) {
        for (var endpoint : Endpoint.values()) {
            uncheckedRequests.put(endpoint, new Requester(spec, endpoint));
        }
    }

    /**
     * Retrieves the requester for the specified endpoint.
     * <p>
     * This method returns the Requester instance associated with the given endpoint.
     * The requester can be used to make unvalidated API calls to that specific endpoint.
     * </p>
     * 
     * @param endpoint the endpoint for which to retrieve the requester
     * @return the Requester instance for the specified endpoint
     */
    public Requester getRequest(Endpoint endpoint) {
        return uncheckedRequests.get(endpoint);
    }

}
