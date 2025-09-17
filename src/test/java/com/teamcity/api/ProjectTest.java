package com.teamcity.api;

import com.teamcity.api.generators.RandomData;
import com.teamcity.api.models.Project;
import com.teamcity.api.models.Role;
import com.teamcity.api.models.Roles;
import com.teamcity.api.models.comparison.ModelAssertions;
import com.teamcity.api.requests.withS.RequesterWithS;
import com.teamcity.api.requests.withoutS.Requester;
import com.teamcity.api.spec.RequestSpecs;
import com.teamcity.api.spec.ResponseSpecs;
import io.qameta.allure.Feature;
import static io.qameta.allure.Allure.step;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static com.teamcity.api.enums.Endpoint.PROJECTS;
import static com.teamcity.api.enums.Endpoint.USERS;
import static com.teamcity.api.generators.TestDataGenerator.generate;
import static com.teamcity.api.enums.UserRole.PROJECT_VIEWER;
import static com.teamcity.api.enums.UserRole.PROJECT_DEVELOPER;
import static com.teamcity.api.enums.UserRole.AGENT_MANAGER;

import com.teamcity.api.enums.UserRole;

@Feature("Project")
public class ProjectTest extends BaseApiTest {

    private static final int PROJECT_ID_CHARACTERS_LIMIT = 225;
    private final ThreadLocal<RequesterWithS<Project>> projectRequestWithS = new ThreadLocal<>();
    private final ThreadLocal<Requester> projectRequest = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void getRequests() {
        projectRequestWithS.set(new RequesterWithS<>(RequestSpecs.authSpec(testData.get().getUser()), PROJECTS));
        projectRequest.set(new Requester(RequestSpecs.authSpec(testData.get().getUser()), PROJECTS));
    }

    @DataProvider(name = "validProjectScenarios")
    public Object[][] validProjectScenarios() {
        return new Object[][]{
            {"Repeating symbols in ID", "aaaabbbbcccc", null},
            {"Maximum ID length (225 symbols)", RandomData.getString(PROJECT_ID_CHARACTERS_LIMIT), null},
            {"Latin letters and digits in ID", "Project123Test456", null},
            {"Single valid symbol in ID", "a", null},
            {"Long name (more than 225 symbols)", null, RandomData.getString(PROJECT_ID_CHARACTERS_LIMIT + 10)},
            {"Cyrillic symbols in name", null, "ТестовыйПроект"}
        };
    }

    @DataProvider(name = "invalidIdScenarios")
    public Object[][] invalidIdScenarios() {
        return new Object[][]{
            {"Empty ID", "", HttpStatus.SC_INTERNAL_SERVER_ERROR},
            {"ID starting with number", "123Project", HttpStatus.SC_INTERNAL_SERVER_ERROR},
            {"ID with invalid symbols", "Project@#$%", HttpStatus.SC_INTERNAL_SERVER_ERROR},
            {"ID with Cyrillic symbols", "Проект", HttpStatus.SC_INTERNAL_SERVER_ERROR},
            {"ID starting with underscore", "_Project", HttpStatus.SC_INTERNAL_SERVER_ERROR},
            {"ID exceeding max length", RandomData.getString(PROJECT_ID_CHARACTERS_LIMIT + 1), HttpStatus.SC_INTERNAL_SERVER_ERROR}
        };
    }

    @DataProvider(name = "rolePermissionScenarios")
    public Object[][] rolePermissionScenarios() {
        return new Object[][]{
            {"Project Viewer", PROJECT_VIEWER},
            {"Project Developer", PROJECT_DEVELOPER},
            {"Agent Manager", AGENT_MANAGER}
        };
    }

    @Test(description = "User should be able to create project", groups = {"Regression"})
    public void userCreatesProjectTest() {
        step("Create user", () -> {
            superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());
        });

        step("Create project", () -> {
            var project = projectRequestWithS.get().create(testData.get().getNewProjectDescription());
            ModelAssertions.assertThatModels(testData.get().getNewProjectDescription(), project).match();
        });
    }

    @Test(description = "User should not be able to create two projects with the same id", groups = {"Regression"})
    public void userCreatesTwoProjectsWithSameIdTest() {
        step("Create user", () -> {
            superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());
        });

        step("Create first project", () -> {
            projectRequestWithS.get().create(testData.get().getNewProjectDescription());
        });

        step("Attempt to create second project with same ID", () -> {
            var secondTestData = generate();
            secondTestData.getNewProjectDescription().setId(testData.get().getNewProjectDescription().getId());

            projectRequest.get().create(secondTestData.getNewProjectDescription())
                    .then().assertThat().spec(ResponseSpecs.requestReturnsBadRequestWithDuplicateId());
        });
    }

    @Test(description = "User should not be able to create project with id exceeding the limit", groups = {"Regression"})
    public void userCreatesProjectWithIdExceedingLimitTest() {
        step("Create user", () -> {
            superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());
        });

        step("Attempt to create project with ID exceeding limit", () -> {
            testData.get().getNewProjectDescription().setId(RandomData.getString(PROJECT_ID_CHARACTERS_LIMIT + 1));

            projectRequest.get().create(testData.get().getNewProjectDescription())
                    .then().assertThat().spec(ResponseSpecs.requestReturnsInternalServerError());
        });

        step("Create project with valid ID length", () -> {
            testData.get().getNewProjectDescription().setId(RandomData.getString(PROJECT_ID_CHARACTERS_LIMIT));
            projectRequestWithS.get().create(testData.get().getNewProjectDescription());
        });
    }

    @Test(description = "Unauthorized user should not be able to create project", groups = {"Regression"})
    public void unauthorizedUserCreatesProjectTest() {
        step("Attempt to create project without authentication", () -> {
            var unauthProjectRequest = new Requester(RequestSpecs.unauthSpec(), PROJECTS);
            unauthProjectRequest.create(testData.get().getNewProjectDescription())
                    .then().assertThat().spec(ResponseSpecs.requestReturnsUnauthorized());
        });

        step("Verify project was not created", () -> {
            superUserRequester.getRequest(PROJECTS)
                    .read(testData.get().getProject().getId())
                    .then().assertThat().spec(ResponseSpecs.requestReturnsNotFoundWithEntityNotFound());
        });
    }

    @Test(description = "User should be able to delete project", groups = {"Regression"})
    public void userDeletesProjectTest() {
        step("Create user", () -> {
            superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());
        });

        step("Create project", () -> {
            projectRequestWithS.get().create(testData.get().getNewProjectDescription());
        });

        step("Delete project", () -> {
            projectRequestWithS.get().delete(testData.get().getProject().getId());
        });

        step("Verify project was deleted", () -> {
            projectRequest.get().read(testData.get().getProject().getId())
                    .then().assertThat().spec(ResponseSpecs.requestReturnsNotFoundWithEntityNotFound());
        });
    }

    @Test(description = "User should be able to create projects with various valid ID and name scenarios",
          groups = {"Regression"}, dataProvider = "validProjectScenarios")
    public void userCreatesProjectWithValidDataTest(String scenario, String customId, String customName) {
        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());

        step("Create project with " + scenario, () -> {
            if (customId != null) {
                testData.get().getNewProjectDescription().setId(customId);
            }
            if (customName != null) {
                testData.get().getNewProjectDescription().setName(customName);
            }
            var project = projectRequestWithS.get().create(testData.get().getNewProjectDescription());
            ModelAssertions.assertThatModels(testData.get().getNewProjectDescription(), project).match();
        });
    }

    @Test(description = "User should be able to create a copy of a project", groups = {"Regression"})
    public void userCreatesCopyOfProjectTest() {
        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());

        step("Create original project", () -> {
            projectRequestWithS.get().create(testData.get().getNewProjectDescription());
        });

        step("Create copy of existing project", () -> {
            var copyProjectData = generate();
            copyProjectData.getNewProjectDescription().setId("copy_of_" + testData.get().getNewProjectDescription().getId());
            copyProjectData.getNewProjectDescription().setName("Copy of " + testData.get().getNewProjectDescription().getName());
            copyProjectData.getNewProjectDescription().setParentProject(testData.get().getProject());

            var copiedProject = projectRequestWithS.get().create(copyProjectData.getNewProjectDescription());
            ModelAssertions.assertThatModels(copyProjectData.getNewProjectDescription(), copiedProject).match();
        });
    }


    @Test(description = "User should not be able to create projects with invalid ID scenarios",
          groups = {"Regression"}, dataProvider = "invalidIdScenarios")
    public void userCannotCreateProjectWithInvalidIdTest(String scenario, String invalidId, int expectedStatusCode) {
        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());

        step("Attempt to create project with " + scenario, () -> {
            testData.get().getNewProjectDescription().setId(invalidId);
            projectRequest.get().create(testData.get().getNewProjectDescription())
                    .then().assertThat().spec(ResponseSpecs.requestReturnsInternalServerError());
        });
    }

    @Test(description = "User should not be able to create a project with empty name", groups = {"Regression"})
    public void userCannotCreateProjectWithEmptyNameTest() {
        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());

        step("Attempt to create project with empty name", () -> {
            testData.get().getNewProjectDescription().setName("");
            projectRequest.get().create(testData.get().getNewProjectDescription())
                    .then().assertThat().spec(ResponseSpecs.requestReturnsBadRequest());
        });
    }

    @Test(description = "User should not be able to create a project with empty id and name", groups = {"Regression"})
    public void userCannotCreateProjectWithEmptyIdAndNameTest() {
        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());

        step("Attempt to create project with empty ID and name", () -> {
            testData.get().getNewProjectDescription().setId("");
            testData.get().getNewProjectDescription().setName("");
            projectRequest.get().create(testData.get().getNewProjectDescription())
                    .then().assertThat().spec(ResponseSpecs.requestReturnsBadRequest());
        });
    }

    @Test(description = "User should not be able to create a project with invalid id and empty name", groups = {"Regression"})
    public void userCannotCreateProjectWithInvalidIdAndEmptyNameTest() {
        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());

        step("Attempt to create project with invalid ID and empty name", () -> {
            testData.get().getNewProjectDescription().setId("123Invalid");
            testData.get().getNewProjectDescription().setName("");
            projectRequest.get().create(testData.get().getNewProjectDescription())
                    .then().assertThat().spec(ResponseSpecs.requestReturnsBadRequest());
        });
    }


    @Test(description = "User should not be able to create 2 projects with the same name", groups = {"Regression"})
    public void userCannotCreateTwoProjectsWithSameNameTest() {
        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());

        step("Create first project with specific name", () -> {
            projectRequestWithS.get().create(testData.get().getNewProjectDescription());
        });

        step("Attempt to create second project with same name", () -> {
            var secondTestData = generate();
            secondTestData.getNewProjectDescription().setName(testData.get().getNewProjectDescription().getName());

            projectRequest.get().create(secondTestData.getNewProjectDescription())
                    .then().assertThat().spec(ResponseSpecs.requestReturnsBadRequestWithDuplicateName());
        });
    }

    @Test(description = "User should not be able to create a copy of non existing project", groups = {"Regression"})
    public void userCannotCreateCopyOfNonExistingProjectTest() {
        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());

        step("Attempt to create copy of non-existing project", () -> {
            var copyProjectData = generate();
            copyProjectData.getNewProjectDescription().setId("copy_of_nonexistent");
            copyProjectData.getNewProjectDescription().setName("Copy of Nonexistent");
            copyProjectData.getNewProjectDescription().setParentProject(new Project("nonexistent", "Nonexistent", null));

            projectRequest.get().create(copyProjectData.getNewProjectDescription())
                    .then().assertThat().spec(ResponseSpecs.requestReturnsNotFound());
        });
    }

    @Test(description = "User should not be able to create a copy with empty info about source project", groups = {"Regression"})
    public void userCannotCreateCopyWithEmptySourceProjectInfoTest() {
        superUserRequesterWithS.getRequest(USERS).create(testData.get().getUser());

        step("Attempt to create copy with empty source project info", () -> {
            var copyProjectData = generate();
            copyProjectData.getNewProjectDescription().setId("copy_with_empty_source");
            copyProjectData.getNewProjectDescription().setName("Copy with Empty Source");
            copyProjectData.getNewProjectDescription().setParentProject(new Project("", "", null));

            projectRequest.get().create(copyProjectData.getNewProjectDescription())
                    .then().assertThat().spec(ResponseSpecs.requestReturnsNotFound());
        });
    }


    @Test(description = "Users with insufficient roles should not be able to create projects",
          groups = {"Regression"}, dataProvider = "rolePermissionScenarios")
    public void userWithInsufficientRoleCannotCreateProjectTest(String roleName, UserRole userRole) {
        step("Create user with " + roleName + " role", () -> {
            var user = generate().getUser();
            user.setRoles(Roles.builder().role(List.of(Role.builder().roleId(userRole).scope("g").build())).build());
            superUserRequesterWithS.getRequest(USERS).create(user);

            var projectRequest = new Requester(RequestSpecs.authSpec(user), PROJECTS);

            step("Attempt to create project as " + roleName, () -> {
                projectRequest.create(testData.get().getNewProjectDescription())
                        .then().assertThat().spec(ResponseSpecs.requestReturnsForbiddenWithAccessDenied());
            });
        });
    }
}
