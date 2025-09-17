package com.teamcity.api;

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
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.teamcity.api.enums.Endpoint.PROJECTS;
import static com.teamcity.api.enums.Endpoint.USERS;
import static com.teamcity.api.generators.TestDataGenerator.generate;

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

        // Use DTO comparison framework instead of manual field comparison
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

        // Use DTO comparison framework instead of manual field comparison
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

}
