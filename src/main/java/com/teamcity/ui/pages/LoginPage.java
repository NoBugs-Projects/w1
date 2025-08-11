package com.teamcity.ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.teamcity.api.config.Config;
import com.teamcity.api.models.User;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

public class LoginPage extends BasePage {

    private static final String LOGIN_URL = "/login.html";
    // css селектор по id
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    // css селектор по className
    private final SelenideElement loginButton = $(".loginButton");

    @Step("Open login page")
    public static LoginPage open() {
        // Открыть указанный url и вернуть new LoginPage()
        return Selenide.open(LOGIN_URL, LoginPage.class);
    }

    @Step("Open super user login page")
    public static LoginPage openSuperUser() {
        return Selenide.open(LOGIN_URL + "?super=1", LoginPage.class);
    }

    @Step("Login as {user.username}")
    public ProjectsPage login(User user) {
        // Метод val(text) заменяет 2 действия: clear и sendKeys
        usernameInput.val(user.getUsername());
        passwordInput.val(user.getPassword());
        loginButton.click();
        // Аналогично new ProjectsPage(). Все методы имеют return для реализации паттерна fluent page object
        return page(ProjectsPage.class);
    }

    @Step("Login as super user")
    public ProjectsPage login() {
        passwordInput.val(Config.getProperty("superUserToken"));
        loginButton.click();
        return page(ProjectsPage.class);
    }

}
