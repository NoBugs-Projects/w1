package com.teamcity.ui;

import com.teamcity.api.models.BuildType;
import com.teamcity.api.models.Project;
import com.teamcity.api.models.comparison.ModelAssertions;
import com.teamcity.api.requests.withS.RequesterWithS;
import com.teamcity.api.spec.RequestSpecs;
import com.teamcity.ui.annotations.BrowserFilter;
import com.teamcity.ui.annotations.Browsers;
import com.teamcity.ui.annotations.UserSession;
import com.teamcity.ui.pages.ProjectsPage;
import com.teamcity.ui.pages.admin.CreateProjectPage;
import com.teamcity.ui.pages.admin.EditBuildTypePage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static io.qameta.allure.Allure.step;

import static com.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.teamcity.api.enums.Endpoint.PROJECTS;

@Feature("Project")
@BrowserFilter
public class CreateProjectTest extends BaseUiTest {

    @Test(description = "User should be able to create project", groups = {"Regression"})
    @Browsers({"chrome", "firefox"})
    @UserSession
    public void userCreatesProject(String ignoredBrowser) {
        step("Create project from Git repository", () -> {
            CreateProjectPage.open(testData.get().getNewProjectDescription().getParentProject().getLocator())
                    .createFrom(GIT_URL)
                    .setupProject(testData.get().getProject().getName(), testData.get().getBuildType().getName());
        });

        var createdBuildTypeId = step("Get created build type ID", () -> {
            return EditBuildTypePage.open().getBuildTypeId();
        });

        step("Verify build type via API", () -> {
            var checkedBuildTypeRequest = new RequesterWithS<BuildType>(RequestSpecs.authSpec(testData.get().getUser()), BUILD_TYPES);
            var buildType = checkedBuildTypeRequest.read(createdBuildTypeId);

            ModelAssertions.assertThatModels(testData.get().getProject(), buildType.getProject()).match();
            ModelAssertions.assertThatModels(testData.get().getBuildType(), buildType).match();
        });

        var createdProjectId = step("Get created project ID from UI", () -> {
            return ProjectsPage.open()
                    .verifyProjectAndBuildType(testData.get().getProject().getName(), testData.get().getBuildType().getName())
                    .getProjectId();
        });

        step("Verify project via API", () -> {
            var checkedProjectRequest = new RequesterWithS<Project>(RequestSpecs.authSpec(testData.get().getUser()), PROJECTS);
            var project = checkedProjectRequest.read(createdProjectId);
            ModelAssertions.assertThatModels(testData.get().getProject(), project).match();
        });
    }

    @Test(description = "User should not be able to create project without name", groups = {"Regression"})
    @Browsers({"chrome"})
    @UserSession
    public void userCreatesProjectWithoutName(String ignoredBrowser) {
        step("Attempt to create project with empty name", () -> {
            CreateProjectPage.open(testData.get().getNewProjectDescription().getParentProject().getLocator())
                    .createFrom(GIT_URL)
                    .setupProject("", testData.get().getBuildType().getName())
                    .verifyProjectNameError("Project name must not be empty");
        });
    }

}
