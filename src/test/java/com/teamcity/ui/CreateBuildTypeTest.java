package com.teamcity.ui;

import com.teamcity.api.models.BuildType;
import com.teamcity.api.models.comparison.ModelAssertions;
import com.teamcity.api.requests.withS.RequesterWithS;
import com.teamcity.api.spec.RequestSpecs;
import com.teamcity.ui.annotations.Browsers;
import com.teamcity.ui.annotations.UserSession;
import com.teamcity.ui.pages.admin.CreateBuildTypePage;
import com.teamcity.ui.pages.admin.EditBuildTypePage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static com.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.teamcity.api.enums.Endpoint.PROJECTS;
import static io.qameta.allure.Allure.step;

@Feature("Build type")
public class CreateBuildTypeTest extends BaseUiTest {

    @Test(description = "User should be able to create build type", groups = {"Regression"})
    @Browsers({"chrome", "firefox"})
    @UserSession
    public void userCreatesBuildTypeTest(String ignoredBrowser) {
        step("Create project for build type", () -> {
            superUserRequesterWithS.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());
        });

        step("Create build type from Git repository", () -> {
            CreateBuildTypePage.open(testData.get().getProject().getId())
                    .createFrom(GIT_URL)
                    .setupBuildType(testData.get().getBuildType().getName());
        });

        var createdBuildTypeId = step("Get created build type ID", () -> {
            return EditBuildTypePage.open().getBuildTypeId();
        });

        step("Verify build type via API", () -> {
            var checkedBuildTypeRequest = new RequesterWithS<BuildType>(RequestSpecs.authSpec(testData.get().getUser()), BUILD_TYPES);
            var buildType = checkedBuildTypeRequest.read(createdBuildTypeId);
            ModelAssertions.assertThatModels(testData.get().getBuildType(), buildType).match();
        });
    }

    @Test(description = "User should not be able to create build type without name", groups = {"Regression"})
    @Browsers({"chrome", "firefox"})
    @UserSession
    public void userCreatesBuildTypeWithoutName(String ignoredBrowser) {
        step("Create project for build type", () -> {
            superUserRequesterWithS.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());
        });

        step("Attempt to create build type with empty name", () -> {
            CreateBuildTypePage.open(testData.get().getProject().getId())
                    .createFrom(GIT_URL)
                    .setupBuildType("")
                    .verifyBuildTypeNameError("Build configuration name must not be empty");
        });
    }

}
