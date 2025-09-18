package com.teamcity.ui;

import com.teamcity.api.models.Build;
import com.teamcity.api.models.comparison.ModelAssertions;
import com.teamcity.api.requests.withS.RequesterWithS;
import com.teamcity.api.spec.RequestSpecs;
import com.teamcity.ui.annotations.UserSession;
import com.teamcity.ui.pages.ProjectsPage;
import com.teamcity.ui.pages.admin.CreateBuildTypeStepPage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static io.qameta.allure.Allure.step;

import static com.teamcity.api.enums.Endpoint.BUILDS;
import static com.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.teamcity.api.enums.Endpoint.PROJECTS;

@Feature("Start build")
public class StartBuildTest extends BaseUiTest {

    @Test(description = "User should be able to create build type step and start build", groups = {"Regression"})
    public void userCreatesBuildTypeStepAndStartsBuildTest(String ignoredBrowser) {
        loginAs(testData.get().getUser());

        step("Create project and build type via API", () -> {
            superUserRequesterWithS.getRequest(PROJECTS).create(testData.get().getNewProjectDescription());
            superUserRequesterWithS.getRequest(BUILD_TYPES).create(testData.get().getBuildType());
        });

        step("Create command line build step", () -> {
            CreateBuildTypeStepPage.open(testData.get().getBuildType().getId())
                    .createCommandLineBuildStep("echo 'Hello World!'");
        });

        var createdBuildId = step("Run build and wait for completion", () -> {
            // Тесты реализованы по паттерну fluent page object, поэтому эта запись выглядит как билдер, в одну строчку
            return ProjectsPage.open()
                    .verifyProjectAndBuildType(testData.get().getProject().getName(), testData.get().getBuildType().getName())
                    .runBuildAndWaitUntilItIsFinished()
                    .getBuildId();
        });

        step("Verify build state and status via API", () -> {
            var checkedBuildRequest = new RequesterWithS<Build>(RequestSpecs
                    .authSpec(testData.get().getUser()), BUILDS);
            // Каждое действие на UI всегда проверяется через API
            var build = checkedBuildRequest.read(createdBuildId);

            // Create expected build with finished state and success status
            var expectedBuild = Build.builder()
                    .state("finished")
                    .status("SUCCESS")
                    .build();

            ModelAssertions.assertThatModels(expectedBuild, build).match();
        });
    }

}
