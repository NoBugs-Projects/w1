package com.teamcity.ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.teamcity.api.config.Config;
import com.teamcity.api.models.User;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

/**
 * Page object for the TeamCity login page.
 * <p>
 * This class represents the login page of the TeamCity application and provides
 * methods for user authentication. It supports both regular user login and
 * super user login functionality.
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
 * @see User
 */
public class LoginPage extends BasePage {

    /**
     * The URL path for the login page.
     */
    private static final String LOGIN_URL = "/login.html";
    
    /**
     * Username input field element.
     * <p>
     * This element is located using CSS selector by ID.
     * </p>
     */
    // css селектор по id
    private final SelenideElement usernameInput = $("#username");
    
    /**
     * Password input field element.
     * <p>
     * This element is located using CSS selector by ID.
     * </p>
     */
    private final SelenideElement passwordInput = $("#password");
    
    /**
     * Login button element.
     * <p>
     * This element is located using CSS selector by class name.
     * </p>
     */
    // css селектор по className
    private final SelenideElement loginButton = $(".loginButton");

    /**
     * Opens the login page and returns a new LoginPage instance.
     * <p>
     * This static method navigates to the login page URL and returns a new
     * LoginPage instance. The page constructor will automatically verify
     * that the page has loaded completely.
     * </p>
     * 
     * @return a new LoginPage instance
     */
    @Step("Open login page")
    public static LoginPage open() {
        // Открыть указанный url и вернуть new LoginPage()
        return Selenide.open(LOGIN_URL, LoginPage.class);
    }

    /**
     * Opens the super user login page and returns a new LoginPage instance.
     * <p>
     * This static method navigates to the super user login page URL (with
     * super=1 parameter) and returns a new LoginPage instance. This is used
     * for administrative operations that require super user privileges.
     * </p>
     * 
     * @return a new LoginPage instance for super user login
     */
    @Step("Open super user login page")
    public static LoginPage openSuperUser() {
        return Selenide.open(LOGIN_URL + "?super=1", LoginPage.class);
    }

    /**
     * Performs user login with the provided user credentials.
     * <p>
     * This method fills in the username and password fields with the provided
     * user's credentials and clicks the login button. It then navigates to
     * the ProjectsPage after successful authentication.
     * </p>
     * 
     * @param user the user object containing login credentials
     * @return a new ProjectsPage instance after successful login
     */
    @Step("Login as {user.username}")
    public ProjectsPage login(User user) {
        // Метод val(text) заменяет 2 действия: clear и sendKeys
        usernameInput.val(user.getUsername());
        passwordInput.val(user.getPassword());
        loginButton.click();
        // Аналогично new ProjectsPage(). Все методы имеют return для реализации паттерна fluent page object
        return page(ProjectsPage.class);
    }

    /**
     * Performs super user login using the configured super user token.
     * <p>
     * This method fills in the password field with the super user token from
     * the configuration and clicks the login button. It then navigates to
     * the ProjectsPage after successful authentication.
     * </p>
     * 
     * @return a new ProjectsPage instance after successful super user login
     */
    @Step("Login as super user")
    public ProjectsPage login() {
        passwordInput.val(Config.getProperty("superUserToken"));
        loginButton.click();
        return page(ProjectsPage.class);
    }

}
