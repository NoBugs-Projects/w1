package com.teamcity.ui;

import com.teamcity.api.models.BuildType;
import com.teamcity.api.requests.checked.CheckedBase;
import com.teamcity.api.spec.Specifications;
import com.teamcity.ui.annotations.UserSession;
import com.teamcity.ui.pages.admin.CreateBuildTypePage;
import com.teamcity.ui.pages.admin.EditBuildTypePage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static com.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.teamcity.api.enums.Endpoint.PROJECTS;

@Feature("Build type")
public class CreateBuildTypeTest extends BaseUiTest {

    @Test(description = "User should be able to create build type", groups = {"Regression"})
    public void userCreatesBuildTypeTest(String ignoredBrowser) {
        checkedSuperUser.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());
        loginAs(testData.get().getUser());

        CreateBuildTypePage.open(testData.get().getProject().getId())
                .createFrom(GIT_URL)
                .setupBuildType(testData.get().getBuildType().getName());
        var createdBuildTypeId = EditBuildTypePage.open().getBuildTypeId();

        var checkedBuildTypeRequest = new CheckedBase<BuildType>(Specifications.getSpec()
                .authSpec(testData.get().getUser()), BUILD_TYPES);
        var buildType = checkedBuildTypeRequest.read(createdBuildTypeId);
        softy.assertThat(buildType.getName()).as("buildTypeName").isEqualTo(testData.get().getBuildType().getName());
    }

    @Test(description = "User should not be able to create build type without name", groups = {"Regression"})
    public void userCreatesBuildTypeWithoutName(String ignoredBrowser) {
        checkedSuperUser.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());
        loginAs(testData.get().getUser());

        CreateBuildTypePage.open(testData.get().getProject().getId())
                .createFrom(GIT_URL)
                .setupBuildType("")
                .verifyBuildTypeNameError("Build configuration name must not be empty");
    }

}
