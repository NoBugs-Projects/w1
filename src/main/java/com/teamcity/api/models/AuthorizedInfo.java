package com.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents authorization information for a TeamCity entity.
 * <p>
 * This class models authorization status information in the TeamCity system.
 * It is typically used to indicate whether a user or agent is authorized
 * to perform certain operations.
 * </p>
 * 
 * <p>
 * The class uses Lombok annotations for automatic generation of getters, setters,
 * constructors, and other boilerplate code. It also uses Jackson annotations for
 * JSON serialization/deserialization with the TeamCity API.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see BaseModel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizedInfo extends BaseModel {

    /**
     * The authorization status.
     * <p>
     * This field indicates whether the entity is authorized to perform
     * the requested operation. The default value is true.
     * </p>
     */
    @Builder.Default
    private Boolean status = true;

}
