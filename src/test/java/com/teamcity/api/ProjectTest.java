package com.teamcity.api;

import com.teamcity.api.annotations.ManualTest;
import com.teamcity.api.generators.RandomData;
import com.teamcity.api.models.Project;
import com.teamcity.api.models.NewProjectDescription;
import com.teamcity.api.models.comparison.ModelAssertions;
import com.teamcity.api.requests.checked.CheckedBase;
import com.teamcity.api.requests.unchecked.UncheckedBase;
import com.teamcity.api.spec.Specifications;
import io.qameta.allure.Feature;
import static io.qameta.allure.Allure.step;
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

        ModelAssertions.assertThatModels(testData.get().getNewProjectDescription(), project).match();
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

    // ========== POSITIVE TESTS (Project creation with correct data) ==========

    @Test(description = "User should be able to create a project if id includes repeating symbols", groups = {"Manual"})
    @ManualTest
    public void userCreatesProjectWithRepeatingSymbolsInIdTest() {
        step("Create project with repeating symbols in ID");
        step("Verify project created successfully");
    }

    @Test(description = "User should be able to create a project if id has 225 symbols", groups = {"Manual"})
    @ManualTest
    public void userCreatesProjectWithMaxIdLengthTest() {
        step("Create project with maximum ID length (225 symbols)");
        step("Verify project created successfully");
    }

    @Test(description = "User should be able to create a project if id includes latin letters, digits", groups = {"Manual"})
    @ManualTest
    public void userCreatesProjectWithLatinLettersAndDigitsInIdTest() {
        step("Create project with Latin letters and digits in ID");
        step("Verify project created successfully");
    }

    @Test(description = "User should be able to create a project if id includes 1 valid symbol", groups = {"Manual"})
    @ManualTest
    public void userCreatesProjectWithSingleValidSymbolInIdTest() {
        step("Create project with single valid symbol in ID");
        step("Verify project created successfully");
    }

    @Test(description = "User should be able to create a project if name has more than 225 symbols", groups = {"Manual"})
    @ManualTest
    public void userCreatesProjectWithLongNameTest() {
        step("Create project with long name (more than 225 symbols)");
        step("Verify project created successfully");
    }

    @Test(description = "User should be able to create a project if name has cyrillic symbols", groups = {"Manual"})
    @ManualTest
    public void userCreatesProjectWithCyrillicSymbolsInNameTest() {
        step("Create project with Cyrillic symbols in name");
        step("Verify project created successfully");
    }

    @Test(description = "User should be able to create a project with 'copyAllAssociatedSettings' false", groups = {"Manual"})
    @ManualTest
    public void userCreatesProjectWithCopyAllAssociatedSettingsFalseTest() {
        step("Create project with copyAllAssociatedSettings set to false");
        step("Verify project created successfully");
    }

    @Test(description = "User should be able to create a copy of a project", groups = {"Manual"})
    @ManualTest
    public void userCreatesCopyOfProjectTest() {
        step("Create copy of existing project");
        step("Verify project copy created successfully");
    }

    // ========== NEGATIVE TESTS (Invalid data for project creation) ==========

    @Test(description = "User should not be able to create a project with empty id", groups = {"Manual"})
    @ManualTest
    public void userCannotCreateProjectWithEmptyIdTest() {
        step("Attempt to create project with empty ID");
        step("Verify project creation failed with bad request");
    }

    @Test(description = "User should not be able to create a project if id starts with number", groups = {"Manual"})
    @ManualTest
    public void userCannotCreateProjectWithIdStartingWithNumberTest() {
        step("Attempt to create project with ID starting with number");
        step("Verify project creation failed with bad request");
    }

    @Test(description = "User should not be able to create a project if id includes invalid symbols", groups = {"Manual"})
    @ManualTest
    public void userCannotCreateProjectWithInvalidSymbolsInIdTest() {
        step("Attempt to create project with invalid symbols in ID");
        step("Verify project creation failed with bad request");
    }

    @Test(description = "User should not be able to create a project if id cyrillic symbols", groups = {"Manual"})
    @ManualTest
    public void userCannotCreateProjectWithCyrillicSymbolsInIdTest() {
        step("Attempt to create project with Cyrillic symbols in ID");
        step("Verify project creation failed with bad request");
    }

    @Test(description = "User should not be able to create a project if id has more than 225 symbols", groups = {"Manual"})
    @ManualTest
    public void userCannotCreateProjectWithIdExceedingMaxLengthTest() {
        step("Attempt to create project with ID exceeding maximum length");
        step("Verify project creation failed with internal server error");
    }

    @Test(description = "User should not be able to create a project if id starts with _", groups = {"Manual"})
    @ManualTest
    public void userCannotCreateProjectWithIdStartingWithUnderscoreTest() {
        step("Attempt to create project with ID starting with underscore");
        step("Verify project creation failed with bad request");
    }

    @Test(description = "User should not be able to create a project with empty name", groups = {"Manual"})
    @ManualTest
    public void userCannotCreateProjectWithEmptyNameTest() {
        step("Attempt to create project with empty name");
        step("Verify project creation failed with bad request");
    }

    @Test(description = "User should not be able to create a project with empty id and name", groups = {"Manual"})
    @ManualTest
    public void userCannotCreateProjectWithEmptyIdAndNameTest() {
        step("Attempt to create project with empty ID and name");
        step("Verify project creation failed with bad request");
    }

    @Test(description = "User should not be able to create a project with invalid id and empty name", groups = {"Manual"})
    @ManualTest
    public void userCannotCreateProjectWithInvalidIdAndEmptyNameTest() {
        step("Attempt to create project with invalid ID and empty name");
        step("Verify project creation failed with bad request");
    }

    // ========== NEGATIVE TESTS (Duplicate project creation) ==========

    @Test(description = "User should not be able to create 2 projects with the same name", groups = {"Manual"})
    @ManualTest
    public void userCannotCreateTwoProjectsWithSameNameTest() {
        step("Create first project with specific name");
        step("Attempt to create second project with same name");
        step("Verify project creation failed with bad request");
    }

    // ========== NEGATIVE TESTS (Project copying) ==========

    @Test(description = "User should not be able to create a copy of non existing project", groups = {"Manual"})
    @ManualTest
    public void userCannotCreateCopyOfNonExistingProjectTest() {
        step("Attempt to create copy of non-existing project");
        step("Verify project copy creation failed with not found");
    }

    @Test(description = "User should not be able to create a copy with empty info about source project", groups = {"Manual"})
    @ManualTest
    public void userCannotCreateCopyWithEmptySourceProjectInfoTest() {
        step("Attempt to create copy with empty source project info");
        step("Verify project copy creation failed with bad request");
    }

    // ========== NEGATIVE TESTS (Roles and permissions) ==========

    @Test(description = "User should not be able to create project as Project Viewer", groups = {"Manual"})
    @ManualTest
    public void projectViewerCannotCreateProjectTest() {
        step("Attempt to create project as Project Viewer");
        step("Verify project creation failed with forbidden");
    }

    @Test(description = "User should not be able to create project as Project Developer", groups = {"Manual"})
    @ManualTest
    public void projectDeveloperCannotCreateProjectTest() {
        step("Attempt to create project as Project Developer");
        step("Verify project creation failed with forbidden");
    }

    @Test(description = "User should not be able to create project as Agent Manager", groups = {"Manual"})
    @ManualTest
    public void agentManagerCannotCreateProjectTest() {
        step("Attempt to create project as Agent Manager");
        step("Verify project creation failed with forbidden");
    }



}
