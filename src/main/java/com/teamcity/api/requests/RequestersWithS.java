package com.teamcity.api.requests;

import com.teamcity.api.enums.Endpoint;
import com.teamcity.api.models.BaseModel;
import com.teamcity.api.requests.withS.RequesterWithS;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;

import java.util.EnumMap;

/**
 * Container class for managing validated API requesters with response specifications.
 * <p>
 * This class provides a centralized way to access validated requesters for all
 * TeamCity API endpoints. It creates and manages a collection of RequesterWithS
 * instances, one for each endpoint defined in the Endpoint enum.
 * </p>
 *
 * <p>
 * The class uses an EnumMap for efficient endpoint-to-requester mapping and provides
 * a type-safe interface for retrieving the appropriate requester for any given endpoint.
 * All requesters include response specifications for automatic response validation.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see Endpoint
 * @see RequesterWithS
 * @see BaseModel
 * @see RequestSpecification
 */
@Getter
public final class RequestersWithS {

    /**
     * Map of endpoints to their corresponding validated requesters.
     * <p>
     * This EnumMap provides efficient lookup of RequesterWithS instances by endpoint.
     * Each endpoint from the Endpoint enum has a corresponding RequesterWithS instance
     * that can be used to make validated API calls with automatic response validation.
     * </p>
     */
    private final EnumMap<Endpoint, RequesterWithS<?>> checkedRequests = new EnumMap<>(Endpoint.class);

    /**
     * Constructs a new RequestersWithS instance.
     * <p>
     * This constructor initializes the RequestersWithS by creating a RequesterWithS
     * instance for each endpoint defined in the Endpoint enum, all using the provided
     * request specification.
     * </p>
     *
     * @param spec the REST Assured request specification to use for all requesters
     */
    public RequestersWithS(RequestSpecification spec) {
        // Создаем все виды реквестов (указанные в Endpoint) для юзера, переданного в spec
        for (var endpoint : Endpoint.values()) {
            checkedRequests.put(endpoint, new RequesterWithS<>(spec, endpoint));
        }
    }

    /**
     * Retrieves the validated requester for the specified endpoint.
     * <p>
     * This method returns the RequesterWithS instance associated with the given endpoint.
     * The requester can be used to make validated API calls to that specific endpoint
     * with automatic response validation based on the endpoint's model class.
     * </p>
     *
     * @param <T> the type of model that this requester will handle
     * @param endpoint the endpoint for which to retrieve the requester
     * @return the RequesterWithS instance for the specified endpoint
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseModel> RequesterWithS<T> getRequest(Endpoint endpoint) {
        return (RequesterWithS<T>) checkedRequests.get(endpoint);
    }

}
