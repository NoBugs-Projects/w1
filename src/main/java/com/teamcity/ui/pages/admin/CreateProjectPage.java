package com.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;

public class CreateProjectPage extends CreateBasePage {

    private static final String PROJECT_SHOW_MODE = "createProjectMenu";

    @Step("Open project creation page")
    public static CreateProjectPage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, PROJECT_SHOW_MODE), CreateProjectPage.class);
    }

    @Step("Create project from url")
    public CreateProjectPage createFrom(String url) {
        baseCreateFrom(url);
        return this;
    }

    @Step("Setup project")
    public CreateProjectPage setupProject(String projectName, String buildTypeName) {
        projectNameInput.val(projectName);
        buildTypeNameInput.val(buildTypeName);
        submitButton.click();
        return this;
    }

}
