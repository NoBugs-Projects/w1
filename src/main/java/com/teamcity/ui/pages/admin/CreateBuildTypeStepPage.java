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

/**
 * Page object for the TeamCity build type step creation page.
 * <p>
 * This class represents the build type step creation page in the TeamCity admin
 * interface and provides methods for creating new build steps, particularly
 * command line build steps with custom scripts.
 * </p>
 * 
 * <p>
 * The class extends BasePage and uses Selenide for web element interaction.
 * It implements the Page Object Model pattern for maintainable UI automation.
 * </p>
 * 
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see BasePage
 * @see RandomData
 */
public class CreateBuildTypeStepPage extends BasePage {

    /**
     * URL template for build type step creation pages.
     * <p>
     * This template is used to construct URLs for build type step creation pages,
     * with a placeholder for the build type ID.
     * </p>
     */
    private static final String NEW_BUILD_STEP_URL = "/admin/editRunType.html?id=buildType:%s&runnerId=__NEW_RUNNER__";
    
    /**
     * The runner type for command line build steps.
     */
    private static final String COMMAND_LINE_RUNNER_TYPE = "Command Line";
    
    /**
     * Runner item filter input element.
     * <p>
     * This element is used to filter available runner types.
     * </p>
     */
    private final SelenideElement runnerItemFilterInput = $(byDataTest("runner-item-filter"));
    
    /**
     * Build step name input field element.
     * <p>
     * This element is used for entering the name of the build step.
     * </p>
     */
    private final SelenideElement buildStepNameInput = $("#buildStepName");
    
    /**
     * Custom script line element for CodeMirror editor.
     * <p>
     * This element is used to interact with the CodeMirror editor for custom scripts.
     * </p>
     */
    private final SelenideElement customScriptLine = $(".CodeMirror-code");
    
    /**
     * Custom script input element for CodeMirror editor.
     * <p>
     * This element is the actual textarea used for entering custom scripts.
     * </p>
     */
    private final SelenideElement customScriptInput = $(".CodeMirror textarea");
    
    /**
     * Collection of available runner items.
     * <p>
     * This collection contains all available runner types that can be selected
     * for the build step.
     * </p>
     */
    private final ElementsCollection runnerItems = $$(byDataTest("runner-item"));

    /**
     * Constructs a new CreateBuildTypeStepPage instance.
     * <p>
     * This constructor does not perform page interactions. Element checks
     * should be done in methods that actually use the elements.
     * </p>
     */
    public CreateBuildTypeStepPage() {
        // Constructor should not perform page interactions
        // Element checks should be done in methods that actually use the elements
    }

    /**
     * Opens the build type step creation page for the specified build type.
     * <p>
     * This static method navigates to the build type step creation page URL
     * and returns a new CreateBuildTypeStepPage instance.
     * </p>
     * 
     * @param buildTypeId the ID of the build type to create a step for
     * @return a new CreateBuildTypeStepPage instance
     */
    @Step("Open build type step creation page")
    public static CreateBuildTypeStepPage open(String buildTypeId) {
        return Selenide.open(NEW_BUILD_STEP_URL.formatted(buildTypeId), CreateBuildTypeStepPage.class);
    }

    /**
     * Creates a command line build step with the specified custom script.
     * <p>
     * This method selects the command line runner type, fills in the build step
     * name with a random value, and enters the custom script. It uses a complex
     * interaction pattern to work with the CodeMirror editor.
     * </p>
     * 
     * @param customScript the custom script to execute in the build step
     * @return a new EditBuildTypePage instance after step creation
     */
    @Step("Create command line build step")
    public EditBuildTypePage createCommandLineBuildStep(String customScript) {
        runnerItemFilterInput.shouldBe(visible, BASE_WAITING);
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
