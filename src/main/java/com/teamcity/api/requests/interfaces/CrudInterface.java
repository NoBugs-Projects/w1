package com.teamcity.api.requests.interfaces;

import com.teamcity.api.models.BaseModel;

/**
 * Interface defining standard CRUD (Create, Read, Update, Delete) operations for API entities.
 * <p>
 * This interface provides a contract for implementing standard database-like operations
 * on API entities. It defines the basic operations that most API request classes should
 * support for managing entities in the TeamCity system.
 * </p>
 *
 * <p>
 * All methods return Object to provide flexibility in implementation, allowing
 * different request classes to return appropriate response types based on their
 * specific needs and the API endpoints they interact with.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see BaseModel
 */
public interface CrudInterface {

    /**
     * Creates a new entity in the system.
     * <p>
     * This method sends a POST request to create a new entity using the provided model.
     * The model should contain all necessary data for entity creation.
     * </p>
     *
     * @param model the model containing the data for the new entity
     * @return the response from the create operation, typically the created entity
     */
    Object create(BaseModel model);

    /**
     * Reads an existing entity from the system by its ID.
     * <p>
     * This method sends a GET request to retrieve an entity with the specified ID.
     * The entity must exist in the system for this operation to succeed.
     * </p>
     *
     * @param id the unique identifier of the entity to read
     * @return the response from the read operation, typically the requested entity
     */
    Object read(String id);

    /**
     * Updates an existing entity in the system.
     * <p>
     * This method sends a PUT request to update an entity with the specified ID
     * using the data from the provided model. The entity must exist in the system
     * for this operation to succeed.
     * </p>
     *
     * @param id the unique identifier of the entity to update
     * @param model the model containing the updated data for the entity
     * @return the response from the update operation, typically the updated entity
     */
    Object update(String id, BaseModel model);

    /**
     * Deletes an existing entity from the system.
     * <p>
     * This method sends a DELETE request to remove an entity with the specified ID
     * from the system. The entity must exist in the system for this operation to succeed.
     * </p>
     *
     * @param id the unique identifier of the entity to delete
     * @return the response from the delete operation, typically a confirmation or status
     */
    Object delete(String id);

}
