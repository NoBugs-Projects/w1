package com.teamcity.api.requests.interfaces;

/**
 * Interface defining search operations for API entities.
 * <p>
 * This interface provides a contract for implementing search functionality
 * in API request classes. It defines the basic search operation that allows
 * retrieving entities based on search criteria.
 * </p>
 *
 * <p>
 * The search method returns Object to provide flexibility in implementation,
 * allowing different request classes to return appropriate response types
 * based on their specific search requirements and the API endpoints they interact with.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 */
public interface SearchInterface {

    /**
     * Performs a search operation to retrieve entities.
     * <p>
     * This method sends a GET request to search for entities based on the
     * configured search criteria. The specific search parameters and filters
     * are typically configured in the implementing class or through the
     * request specification.
     * </p>
     *
     * @return the response from the search operation, typically a collection of matching entities
     */
    Object search();

}
