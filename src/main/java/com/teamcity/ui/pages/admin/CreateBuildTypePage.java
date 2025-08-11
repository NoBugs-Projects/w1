package com.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;

public class CreateBuildTypePage extends CreateBasePage {

    private static final String BUILD_TYPE_SHOW_MODE = "createBuildTypeMenu";

    @Step("Open build type creation page")
    public static CreateBuildTypePage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, BUILD_TYPE_SHOW_MODE), CreateBuildTypePage.class);
    }

    @Step("Create build type from url")
    public CreateBuildTypePage createFrom(String url) {
        baseCreateFrom(url);
        return this;
    }

    @Step("Setup build type")
    public CreateBuildTypePage setupBuildType(String buildTypeName) {
        buildTypeNameInput.val(buildTypeName);
        submitButton.click();
        return this;
    }

}
