package com.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;

/**
 * Page object for the TeamCity build type creation page.
 * <p>
 * This class represents the build type creation page in the TeamCity admin interface
 * and provides methods for creating new build types from source URLs. It extends
 * CreateBasePage to inherit common creation functionality.
 * </p>
 *
 * <p>
 * The class uses Selenide for web element interaction and implements the Page Object
 * Model pattern for maintainable UI automation.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see CreateBasePage
 */
public class CreateBuildTypePage extends CreateBasePage {

    /**
     * The show mode parameter for build type creation.
     */
    private static final String BUILD_TYPE_SHOW_MODE = "createBuildTypeMenu";

    /**
     * Opens the build type creation page for the specified project.
     * <p>
     * This static method navigates to the build type creation page URL and returns
     * a new CreateBuildTypePage instance. The page constructor will automatically
     * verify that the page has loaded completely.
     * </p>
     *
     * @param projectId the ID of the project to create the build type in
     * @return a new CreateBuildTypePage instance
     */
    @Step("Open build type creation page")
    public static CreateBuildTypePage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, BUILD_TYPE_SHOW_MODE), CreateBuildTypePage.class);
    }

    /**
     * Creates a build type from the specified URL.
     * <p>
     * This method uses the base creation logic to create a build type from a
     * source URL, including URL input, submission, and connection verification.
     * </p>
     *
     * @param url the source URL to create the build type from
     * @return this CreateBuildTypePage instance for method chaining
     */
    @Step("Create build type from url")
    public CreateBuildTypePage createFrom(String url) {
        baseCreateFrom(url);
        return this;
    }

    /**
     * Sets up the build type with the specified name.
     * <p>
     * This method fills in the build type name field and submits the form
     * to complete the build type creation process.
     * </p>
     *
     * @param buildTypeName the name for the new build type
     * @return this CreateBuildTypePage instance for method chaining
     */
    @Step("Setup build type")
    public CreateBuildTypePage setupBuildType(String buildTypeName) {
        buildTypeNameInput.val(buildTypeName);
        submitButton.click();
        return this;
    }

}
