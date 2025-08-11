package com.teamcity.api;

import com.teamcity.api.generators.RandomData;
import com.teamcity.api.models.Project;
import com.teamcity.api.requests.checked.CheckedBase;
import com.teamcity.api.requests.unchecked.UncheckedBase;
import com.teamcity.api.spec.Specifications;
import io.qameta.allure.Feature;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.teamcity.api.enums.Endpoint.PROJECTS;
import static com.teamcity.api.enums.Endpoint.USERS;
import static com.teamcity.api.generators.TestDataGenerator.generate;

@Feature("Project")
public class ProjectTest extends BaseApiTest {

    private static final int PROJECT_ID_CHARACTERS_LIMIT = 225;
    private final ThreadLocal<CheckedBase<Project>> checkedProjectRequest = new ThreadLocal<>();
    private final ThreadLocal<UncheckedBase> uncheckedProjectRequest = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void getRequests() {
        checkedProjectRequest.set(new CheckedBase<>(Specifications.getSpec()
                .authSpec(testData.get().getUser()), PROJECTS));
        uncheckedProjectRequest.set(new UncheckedBase(Specifications.getSpec()
                .authSpec(testData.get().getUser()), PROJECTS));
    }

    @Test(description = "User should be able to create project", groups = {"Regression"})
    public void userCreatesProjectTest() {
        checkedSuperUser.getRequest(USERS).create(testData.get().getUser());

        var project = checkedProjectRequest.get().create(testData.get().getNewProjectDescription());

        softy.assertThat(project.getId()).as("projectId").isEqualTo(testData.get().getProject().getId());
    }

    @Test(description = "User should not be able to create two projects with the same id", groups = {"Regression"})
    public void userCreatesTwoProjectsWithSameIdTest() {
        checkedSuperUser.getRequest(USERS).create(testData.get().getUser());

        checkedProjectRequest.get().create(testData.get().getNewProjectDescription());

        var secondTestData = generate();
        secondTestData.getNewProjectDescription().setId(testData.get().getNewProjectDescription().getId());

        uncheckedProjectRequest.get().create(secondTestData.getNewProjectDescription())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test(description = "User should not be able to create project with id exceeding the limit", groups = {"Regression"})
    public void userCreatesProjectWithIdExceedingLimitTest() {
        checkedSuperUser.getRequest(USERS).create(testData.get().getUser());

        testData.get().getNewProjectDescription().setId(RandomData.getString(PROJECT_ID_CHARACTERS_LIMIT + 1));

        uncheckedProjectRequest.get().create(testData.get().getNewProjectDescription())
                .then().assertThat().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        testData.get().getNewProjectDescription().setId(RandomData.getString(PROJECT_ID_CHARACTERS_LIMIT));

        checkedProjectRequest.get().create(testData.get().getNewProjectDescription());
    }

    @Test(description = "Unauthorized user should not be able to create project", groups = {"Regression"})
    public void unauthorizedUserCreatesProjectTest() {
        var uncheckedUnauthProjectRequest = new UncheckedBase(Specifications.getSpec()
                .unauthSpec(), PROJECTS);
        uncheckedUnauthProjectRequest.create(testData.get().getNewProjectDescription())
                .then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);

        uncheckedSuperUser.getRequest(PROJECTS)
                .read(testData.get().getProject().getId())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString("Could not find the entity requested"));
    }

    @Test(description = "User should be able to delete project", groups = {"Regression"})
    public void userDeletesProjectTest() {
        checkedSuperUser.getRequest(USERS).create(testData.get().getUser());

        checkedProjectRequest.get().create(testData.get().getNewProjectDescription());
        checkedProjectRequest.get().delete(testData.get().getProject().getId());

        uncheckedProjectRequest.get().read(testData.get().getProject().getId())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString("Could not find the entity requested"));
    }

}
