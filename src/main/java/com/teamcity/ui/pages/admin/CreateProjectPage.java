package com.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;

/**
 * Page object for the TeamCity project creation page.
 * <p>
 * This class represents the project creation page in the TeamCity admin interface
 * and provides methods for creating new projects from source URLs. It extends
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
public class CreateProjectPage extends CreateBasePage {

    /**
     * The show mode parameter for project creation.
     */
    private static final String PROJECT_SHOW_MODE = "createProjectMenu";

    /**
     * Opens the project creation page for the specified parent project.
     * <p>
     * This static method navigates to the project creation page URL and returns
     * a new CreateProjectPage instance. The page constructor will automatically
     * verify that the page has loaded completely.
     * </p>
     * 
     * @param projectId the ID of the parent project to create the new project in
     * @return a new CreateProjectPage instance
     */
    @Step("Open project creation page")
    public static CreateProjectPage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, PROJECT_SHOW_MODE), CreateProjectPage.class);
    }

    /**
     * Creates a project from the specified URL.
     * <p>
     * This method uses the base creation logic to create a project from a
     * source URL, including URL input, submission, and connection verification.
     * </p>
     * 
     * @param url the source URL to create the project from
     * @return this CreateProjectPage instance for method chaining
     */
    @Step("Create project from url")
    public CreateProjectPage createFrom(String url) {
        baseCreateFrom(url);
        return this;
    }

    /**
     * Sets up the project with the specified names.
     * <p>
     * This method fills in both the project name and build type name fields
     * and submits the form to complete the project creation process.
     * </p>
     * 
     * @param projectName the name for the new project
     * @param buildTypeName the name for the new build type
     * @return this CreateProjectPage instance for method chaining
     */
    @Step("Setup project")
    public CreateProjectPage setupProject(String projectName, String buildTypeName) {
        projectNameInput.val(projectName);
        buildTypeNameInput.val(buildTypeName);
        submitButton.click();
        return this;
    }

}
