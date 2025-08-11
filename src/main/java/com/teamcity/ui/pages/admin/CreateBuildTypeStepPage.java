package com.teamcity.ui.pages.admin;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.teamcity.api.generators.RandomData;
import com.teamcity.ui.pages.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.page;
import static com.teamcity.ui.Selectors.byDataTest;

public class CreateBuildTypeStepPage extends BasePage {

    private static final String NEW_BUILD_STEP_URL = "/admin/editRunType.html?id=buildType:%s&runnerId=__NEW_RUNNER__";
    private static final String COMMAND_LINE_RUNNER_TYPE = "Command Line";
    private final SelenideElement runnerItemFilterInput = $(byDataTest("runner-item-filter"));
    private final SelenideElement buildStepNameInput = $("#buildStepName");
    private final SelenideElement customScriptLine = $(".CodeMirror-code");
    private final SelenideElement customScriptInput = $(".CodeMirror textarea");
    private final ElementsCollection runnerItems = $$(byDataTest("runner-item"));

    public CreateBuildTypeStepPage() {
        runnerItemFilterInput.shouldBe(visible, BASE_WAITING);
    }

    @Step("Open build type step creation page")
    public static CreateBuildTypeStepPage open(String buildTypeId) {
        return Selenide.open(NEW_BUILD_STEP_URL.formatted(buildTypeId), CreateBuildTypeStepPage.class);
    }

    @Step("Create command line build step")
    public EditBuildTypePage createCommandLineBuildStep(String customScript) {
        runnerItems.findBy(text(COMMAND_LINE_RUNNER_TYPE)).hover().$(byDataTest("ring-link")).click();
        buildStepNameInput.shouldBe(visible, BASE_WAITING).val(RandomData.getString());
        // Сложный элемент на UI для вставки кастомного скрипта, поэтому пришлось таким трудным путем его заполнять:
        // кликать сначала на один элемент, потом передавать sendKeys (не val) в другой элемент
        customScriptLine.click();
        customScriptInput.sendKeys(customScript);
        submitButton.click();
        return page(EditBuildTypePage.class);
    }

}
