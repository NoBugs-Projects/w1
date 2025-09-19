package com.teamcity.api;

import com.teamcity.api.annotations.ManualTest;
import com.teamcity.api.enums.UserRole;
import com.teamcity.api.generators.RandomData;
import com.teamcity.api.models.BuildType;
import com.teamcity.api.models.Roles;
import com.teamcity.api.models.comparison.ModelAssertions;
import com.teamcity.api.requests.withS.RequesterWithS;
import com.teamcity.api.requests.withoutS.Requester;
import com.teamcity.api.spec.RequestSpecs;
import com.teamcity.api.spec.ResponseSpecs;
import io.qameta.allure.Feature;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.teamcity.api.enums.Endpoint.PROJECTS;
import static com.teamcity.api.enums.Endpoint.USERS;
import static com.teamcity.api.generators.TestDataGenerator.generate;
import static io.qameta.allure.Allure.step;

@Feature("Build type")
public class BuildTypeTest extends BaseApiTest {

    private static final int BUILD_TYPE_ID_CHARACTERS_LIMIT = 225;
    private final ThreadLocal<RequesterWithS<BuildType>> checkedBuildTypeRequest = new ThreadLocal<>();
    private final ThreadLocal<Requester> uncheckedBuildTypeRequest = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void getRequests() {
        checkedBuildTypeRequest.set(new RequesterWithS<>(RequestSpecs.authSpec(testData.get().getUser()), BUILD_TYPES));
        uncheckedBuildTypeRequest.set(new Requester(RequestSpecs.authSpec(testData.get().getUser()), BUILD_TYPES));
    }

    @Test(description = "User should be able to create build type", groups = {"Regression"})
    public void userCreatesBuildTypeTest() {
        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());
        superUserRequesterWithS.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());

        var buildType = checkedBuildTypeRequest.get().create(testData.get().getBuildType());

        ModelAssertions.assertThatModels(testData.get().getBuildType(), buildType).match();
    }

    @Test(description = "User should not be able to create two build types with the same id", groups = {"Regression"})
    public void userCreatesTwoBuildTypesWithSameIdTest() {
        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());
        superUserRequesterWithS.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());

        checkedBuildTypeRequest.get().create(testData.get().getBuildType());

        var secondTestData = generate();
        var buildTypeTestData = testData.get().getBuildType();
        var secondBuildTypeTestData = secondTestData.getBuildType();
        secondBuildTypeTestData.setId(buildTypeTestData.getId());
        secondBuildTypeTestData.setProject(buildTypeTestData.getProject());

        uncheckedBuildTypeRequest.get().create(secondTestData.getBuildType())
                .then().assertThat().spec(ResponseSpecs.requestReturnsBadRequestWithDuplicateId());
    }

    @Test(description = "User should not be able to create build type with id exceeding the limit", groups = {"Regression"})
    public void userCreatesBuildTypeWithIdExceedingLimitTest() {
        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());
        superUserRequesterWithS.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());

        var buildTypeTestData = testData.get().getBuildType();
        buildTypeTestData.setId(RandomData.getString(BUILD_TYPE_ID_CHARACTERS_LIMIT + 1));

        uncheckedBuildTypeRequest.get().create(testData.get().getBuildType())
                .then().assertThat().spec(ResponseSpecs.requestReturnsInternalServerError());

        buildTypeTestData.setId(RandomData.getString(BUILD_TYPE_ID_CHARACTERS_LIMIT));

        checkedBuildTypeRequest.get().create(testData.get().getBuildType());
    }

    @Test(description = "Unauthorized user should not be able to create build type", groups = {"Regression"})
    public void unauthorizedUserCreatesBuildTypeTest() {
        superUserRequesterWithS.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());

        var uncheckedUnauthBuildTypeRequest = new Requester(RequestSpecs.unauthSpec(), BUILD_TYPES);
        uncheckedUnauthBuildTypeRequest.create(testData.get().getBuildType())
                .then().assertThat().spec(ResponseSpecs.requestReturnsUnauthorized());

        superUserRequester.getRequest(BUILD_TYPES).read(testData.get().getBuildType().getId())
                .then().assertThat().spec(ResponseSpecs.requestReturnsNotFoundWithEntityNotFound());
    }

    @Test(description = "User should be able to delete build type", groups = {"Regression"})
    public void userDeletesBuildTypeTest() {
        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());
        superUserRequesterWithS.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());

        checkedBuildTypeRequest.get().create(testData.get().getBuildType());
        checkedBuildTypeRequest.get().delete(testData.get().getBuildType().getId());

        uncheckedBuildTypeRequest.get().read(testData.get().getBuildType().getId())
                .then().assertThat().spec(ResponseSpecs.requestReturnsNotFoundWithEntityNotFound());
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Regression"})
    public void projectAdminCreatesBuildTypeTest() {
        superUserRequesterWithS.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());

        testData.get().getUser().setRoles(generate(Roles.class, UserRole.PROJECT_ADMIN, "p:" + testData.get().getProject().getId()));

        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());

        var buildType = checkedBuildTypeRequest.get().create(testData.get().getBuildType());

        ModelAssertions.assertThatModels(testData.get().getBuildType(), buildType).match();
    }

    @Test(description = "Project admin should not be able to create build type for not their project", groups = {"Regression"})
    public void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        var secondTestData = generate();
        superUserRequesterWithS.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());
        superUserRequesterWithS.getRequest(PROJECTS).create(secondTestData.getNewProjectDescription());

        testData.get().getUser().setRoles(generate(Roles.class, UserRole.PROJECT_ADMIN, "p:" + testData.get().getProject().getId()));
        secondTestData.getUser().setRoles(generate(Roles.class, UserRole.PROJECT_ADMIN, "p:" + secondTestData.getProject().getId()));

        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());
        superUserRequesterWithS.getRequest(USERS).create(secondTestData.getUser());

        uncheckedBuildTypeRequest.get().create(secondTestData.getBuildType())
                .then().assertThat().spec(ResponseSpecs.requestReturnsForbiddenWithAccessDenied());
    }


    @Test
    @ManualTest("Test complete build type lifecycle including creation, configuration, execution, and cleanup")
    public void manualBuildTypeLifecycleManagement() {
        step("Create project for build type", () -> {
            superUserRequesterWithS.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());
        });

        step("Create build type with basic configuration", () -> {
            checkedBuildTypeRequest.get().create(testData.get().getBuildType());
        });

        step("Configure build type settings", () -> {
            // Manual step: Navigate to build type settings page
            // Expected: Build type settings are accessible and configurable
        });

        step("Add build steps to build type", () -> {
            // Manual step: Add various build steps (compile, test, package, deploy)
            // Expected: Build steps are added and configured correctly
        });

        step("Configure build triggers", () -> {
            // Manual step: Set up build triggers (VCS, schedule, dependency)
            // Expected: Build triggers are configured and active
        });

        step("Set up build parameters", () -> {
            // Manual step: Configure build parameters and environment variables
            // Expected: Build parameters are set up and accessible
        });

        step("Configure build features", () -> {
            // Manual step: Enable build features (notifications, artifacts, reports)
            // Expected: Build features are configured and functional
        });

        step("Test build type configuration", () -> {
            // Manual step: Validate build type configuration
            // Expected: Configuration is valid and ready for execution
        });

        step("Trigger manual build", () -> {
            // Manual step: Trigger a manual build execution
            // Expected: Build is queued and starts execution
        });

        step("Monitor build execution", () -> {
            // Manual step: Monitor build progress and logs
            // Expected: Build execution is visible and trackable
        });

        step("Verify build results", () -> {
            // Manual step: Check build results, artifacts, and reports
            // Expected: Build completes successfully with expected outputs
        });

        step("Clean up build type", () -> {
            // Manual step: Archive or delete build type
            // Expected: Build type is properly cleaned up
        });
    }

    /**
     * Manual test: Build type with complex dependencies and integrations.
     * <p>
     * This test covers build type configuration with complex dependencies,
     * integrations, and advanced features.
     * </p>
     */
    @Test
    @ManualTest("Test build type with complex dependencies, integrations, and advanced features")
    public void manualBuildTypeComplexConfiguration() {
        step("Create parent project", () -> {
            superUserRequesterWithS.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());
        });

        step("Create child project", () -> {
            // Manual step: Create child project with dependency on parent
            // Expected: Child project is created with proper dependency
        });

        step("Configure build type with VCS integration", () -> {
            // Manual step: Set up VCS root and checkout rules
            // Expected: VCS integration is configured and functional
        });

        step("Set up build dependencies", () -> {
            // Manual step: Configure snapshot and artifact dependencies
            // Expected: Build dependencies are properly configured
        });

        step("Configure build chains", () -> {
            // Manual step: Set up build chains and triggers
            // Expected: Build chains are configured and working
        });

        step("Set up build notifications", () -> {
            // Manual step: Configure email and other notification rules
            // Expected: Notifications are set up and functional
        });

        step("Configure build artifacts", () -> {
            // Manual step: Set up artifact publishing and retention rules
            // Expected: Artifacts are configured and managed properly
        });

        step("Set up build reports", () -> {
            // Manual step: Configure test reports and coverage reports
            // Expected: Reports are generated and accessible
        });

        step("Test complex build execution", () -> {
            // Manual step: Execute build with all complex configurations
            // Expected: Build executes successfully with all features
        });

        step("Verify integration points", () -> {
            // Manual step: Verify all integration points work correctly
            // Expected: All integrations are functional and reliable
        });
    }

    /**
     * Manual test: Build type performance and scalability testing.
     * <p>
     * This test covers build type performance under various load conditions
     * and scalability scenarios.
     * </p>
     */
    @Test
    @ManualTest("Test build type performance and scalability under various load conditions")
    public void manualBuildTypePerformanceTesting() {
        step("Create test project", () -> {
            superUserRequesterWithS.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());
        });

        step("Configure high-performance build type", () -> {
            // Manual step: Configure build type for optimal performance
            // Expected: Build type is configured for high performance
        });

        step("Set up parallel build execution", () -> {
            // Manual step: Configure parallel build steps and agents
            // Expected: Parallel execution is configured and functional
        });

        step("Configure build caching", () -> {
            // Manual step: Set up build caches and incremental builds
            // Expected: Build caching is configured and working
        });

        step("Test single build performance", () -> {
            // Manual step: Execute single build and measure performance
            // Expected: Single build performance meets requirements
        });

        step("Test concurrent build execution", () -> {
            // Manual step: Execute multiple builds concurrently
            // Expected: Concurrent builds execute without conflicts
        });

        step("Test build queue performance", () -> {
            // Manual step: Test build queue with high load
            // Expected: Build queue handles high load efficiently
        });

        step("Monitor resource utilization", () -> {
            // Manual step: Monitor CPU, memory, and disk usage during builds
            // Expected: Resource utilization is within acceptable limits
        });

        step("Test build scalability", () -> {
            // Manual step: Test build performance with increasing load
            // Expected: Build performance scales appropriately
        });

        step("Optimize build configuration", () -> {
            // Manual step: Optimize build configuration based on performance data
            // Expected: Build configuration is optimized for performance
        });
    }

    /**
     * Manual test: Build type error handling and recovery.
     * <p>
     * This test covers build type error handling, recovery procedures,
     * and failure management.
     * </p>
     */
    @Test
    @ManualTest("Test build type error handling, recovery procedures, and failure management")
    public void manualBuildTypeErrorHandling() {
        step("Create test project", () -> {
            superUserRequesterWithS.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());
        });

        step("Configure build type with error scenarios", () -> {
            // Manual step: Configure build type to test various error conditions
            // Expected: Error scenarios are properly configured
        });

        step("Test build step failure handling", () -> {
            // Manual step: Test build step failure and error reporting
            // Expected: Build step failures are properly handled and reported
        });

        step("Test build timeout handling", () -> {
            // Manual step: Test build timeout scenarios and recovery
            // Expected: Build timeouts are handled gracefully
        });

        step("Test resource failure handling", () -> {
            // Manual step: Test handling of resource failures (disk, network, etc.)
            // Expected: Resource failures are handled and reported appropriately
        });

        step("Test build retry mechanisms", () -> {
            // Manual step: Test build retry and recovery mechanisms
            // Expected: Build retry mechanisms work correctly
        });

        step("Test build failure notifications", () -> {
            // Manual step: Test failure notification systems
            // Expected: Failure notifications are sent to appropriate recipients
        });

        step("Test build cleanup on failure", () -> {
            // Manual step: Test cleanup procedures when builds fail
            // Expected: Failed builds are properly cleaned up
        });

        step("Test build recovery procedures", () -> {
            // Manual step: Test procedures for recovering from build failures
            // Expected: Build recovery procedures are effective
        });

        step("Verify error logging and reporting", () -> {
            // Manual step: Verify error logging and reporting systems
            // Expected: Error information is properly logged and reported
        });
    }

}
