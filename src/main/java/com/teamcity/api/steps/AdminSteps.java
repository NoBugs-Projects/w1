package com.teamcity.api.steps;

import com.teamcity.api.models.User;
import com.teamcity.api.requests.CheckedRequests;
import com.teamcity.api.spec.Specifications;
import io.qameta.allure.Step;

import static com.teamcity.api.enums.Endpoint.USERS;

/**
 * Step definitions for administrative operations in TeamCity.
 * Provides static methods for common admin tasks like user management.
 *
 * <p>Usage example:</p>
 * <pre>
 * // Create a user from test data
 * User createdUser = AdminSteps.createUser(testData.getUser());
 *
 * // Or create a user with specific data
 * User user = new User();
 * user.setUsername("testuser");
 * user.setPassword("password");
 * User createdUser = AdminSteps.createUser(user);
 * </pre>
 *
 * @author TeamCity Testing Framework
 * @since 1.0
 */
public final class AdminSteps {

    /** The super user request specification for admin operations */
    private static final CheckedRequests SUPER_USER_REQUESTS =
            new CheckedRequests(Specifications.getSpec().superUserSpec());

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private AdminSteps() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Creates a new user in TeamCity using super user privileges.
     * This method uses the super user authentication to create users with admin permissions.
     *
     * @param user the user to create
     * @return the created user with server-generated fields populated
     * @throws IllegalArgumentException if user is null
     * @throws RuntimeException if the user creation fails
     */
    @Step("Creating new user: {user.username}")
    public static User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return (User) SUPER_USER_REQUESTS.getRequest(USERS).create(user);
    }
}
