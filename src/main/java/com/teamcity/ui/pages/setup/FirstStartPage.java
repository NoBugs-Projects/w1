package com.teamcity.ui.pages.setup;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.teamcity.ui.pages.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

/**
 * Page object for the TeamCity first start setup page.
 * <p>
 * This class represents the initial setup page that appears when TeamCity
 * is started for the first time. It provides methods for configuring the
 * server, selecting database type, and accepting the license agreement.
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
public class FirstStartPage extends BasePage {

    /**
     * Restore button element for page verification.
     * <p>
     * This element is used to verify that the page has loaded completely.
     * </p>
     */
    private final SelenideElement restoreButton = $("#restoreButton");
    
    /**
     * Proceed button element for navigation.
     * <p>
     * This element is used to proceed through the setup steps.
     * </p>
     */
    private final SelenideElement proceedButton = $("#proceedButton");
    
    /**
     * Database type selection element.
     * <p>
     * This element is used to select the database type for TeamCity.
     * </p>
     */
    private final SelenideElement dbTypeSelect = $("#dbType");
    
    /**
     * License acceptance checkbox element.
     * <p>
     * This element is used to accept the TeamCity license agreement.
     * </p>
     */
    private final SelenideElement acceptLicenseCheckbox = $("#accept");
    
    /**
     * Submit button element for completing setup.
     * <p>
     * This element is used to submit the setup form and complete
     * the initial configuration.
     * </p>
     */
    private final SelenideElement submitButton = $(".continueBlock > .submitButton");

    /**
     * Constructs a new FirstStartPage instance and verifies page load.
     * <p>
     * This constructor verifies that the page has loaded completely by checking
     * that the restore button is visible within the long waiting time.
     * </p>
     */
    public FirstStartPage() {
        restoreButton.shouldBe(visible, LONG_WAITING);
    }

    /**
     * Opens the first start page and returns a new FirstStartPage instance.
     * <p>
     * This static method navigates to the root URL and returns a new
     * FirstStartPage instance. The page constructor will automatically
     * verify that the page has loaded completely.
     * </p>
     * 
     * @return a new FirstStartPage instance
     */
    @Step("Open first start page")
    public static FirstStartPage open() {
        return Selenide.open("/", FirstStartPage.class);
    }

    /**
     * Performs the complete first start setup process.
     * <p>
     * This method automates the entire first start setup process, including
     * proceeding through the setup steps, selecting the database type,
     * accepting the license agreement, and submitting the form.
     * </p>
     * 
     * @return this FirstStartPage instance for method chaining
     */
    @Step("Setup server on first start")
    public FirstStartPage setupFirstStart() {
        proceedButton.click();
        dbTypeSelect.shouldBe(visible, LONG_WAITING);
        proceedButton.click();
        acceptLicenseCheckbox.should(exist, LONG_WAITING).scrollTo().click();
        submitButton.click();
        return this;
    }

}
