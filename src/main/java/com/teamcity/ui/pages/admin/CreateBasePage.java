package com.teamcity.ui.pages.admin;

import com.codeborne.selenide.SelenideElement;
import com.teamcity.ui.pages.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

/**
 * Abstract base class for TeamCity creation pages.
 * <p>
 * This class provides common functionality and shared elements that are used
 * across multiple creation pages in the TeamCity admin interface. It includes
 * common form elements, error handling, and base creation workflows.
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
 */
// Создали родительский класс для страниц создания проекта и билд конфигурации, так как они имеют схожий шаблон
public abstract class CreateBasePage extends BasePage {

    /**
     * URL template for creation pages.
     * <p>
     * This template is used to construct URLs for various creation pages,
     * with placeholders for project ID and show mode.
     * </p>
     */
    protected static final String CREATE_URL = "/admin/createObjectMenu.html?projectId=%s&showMode=%s";

    /**
     * Project name input field element.
     * <p>
     * This element is used for entering the project name during creation.
     * </p>
     */
    protected final SelenideElement projectNameInput = $("#projectName");

    /**
     * Build type name input field element.
     * <p>
     * This element is used for entering the build type name during creation.
     * </p>
     */
    protected final SelenideElement buildTypeNameInput = $("#buildTypeName");

    /**
     * URL input field element.
     * <p>
     * This element is used for entering the source URL during creation.
     * </p>
     */
    private final SelenideElement urlInput = $("#url");

    /**
     * Connection successful message element.
     * <p>
     * This element appears when the connection to the source URL is successful.
     * </p>
     */
    private final SelenideElement connectionSuccessfulMessage = $(".connectionSuccessful");

    /**
     * Project name error message element.
     * <p>
     * This element displays validation errors for the project name field.
     * </p>
     */
    private final SelenideElement projectNameError = $("#error_projectName");

    /**
     * Build type name error message element.
     * <p>
     * This element displays validation errors for the build type name field.
     * </p>
     */
    private final SelenideElement buildTypeNameError = $("#error_buildTypeName");

    /**
     * Protected constructor for abstract base class.
     * <p>
     * This constructor does not perform page interactions. Element checks
     * should be done in methods that actually use the elements.
     * </p>
     */
    protected CreateBasePage() {
        // Constructor should not perform page interactions
        // Element checks should be done in methods that actually use the elements
    }

    /**
     * Abstract method for creating from URL.
     * <p>
     * This method must be implemented by subclasses to provide specific
     * creation logic for different types of entities.
     * </p>
     *
     * @param url the source URL to create from
     * @return this CreateBasePage instance for method chaining
     */
    protected abstract CreateBasePage createFrom(String url);

    /**
     * Base implementation for creating from URL.
     * <p>
     * This method provides the common logic for creating entities from a URL,
     * including URL input, submission, and connection verification.
     * </p>
     *
     * @param url the source URL to create from
     */
    protected final void baseCreateFrom(String url) {
        submitButton.shouldBe(visible, BASE_WAITING);
        urlInput.val(url);
        submitButton.click();
        connectionSuccessfulMessage.should(appear, BASE_WAITING);
    }

    /**
     * Verifies that a project name error message is displayed.
     * <p>
     * This method checks that the project name error element is visible
     * and contains the expected error text.
     * </p>
     *
     * @param error the expected error message
     * @return this CreateBasePage instance for method chaining
     */
    @Step("Verify project name error")
    public CreateBasePage verifyProjectNameError(String error) {
        projectNameError.shouldBe(visible, BASE_WAITING);
        projectNameError.shouldHave(exactText(error));
        return this;
    }

    /**
     * Verifies that a build type name error message is displayed.
     * <p>
     * This method checks that the build type name error element is visible
     * and contains the expected error text.
     * </p>
     *
     * @param error the expected error message
     * @return this CreateBasePage instance for method chaining
     */
    @Step("Verify build type name error")
    public CreateBasePage verifyBuildTypeNameError(String error) {
        buildTypeNameError.shouldBe(visible, BASE_WAITING);
        buildTypeNameError.shouldHave(exactText(error));
        return this;
    }

}
