package com.teamcity.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.teamcity.BaseTest;
import com.teamcity.api.config.Config;
import com.teamcity.api.models.User;
import com.teamcity.api.steps.AdminSteps;
import com.teamcity.ui.annotations.UserSession;
import com.teamcity.ui.pages.LoginPage;
import io.qameta.allure.selenide.AllureSelenide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import java.util.Map;

import static com.teamcity.api.enums.Endpoint.USERS;

public abstract class BaseUiTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseUiTest.class);
    protected static final String GIT_URL = "https://github.com/selenide/selenide.git";

    @BeforeSuite(alwaysRun = true)
    public void setupUiTests() {
        // Нет никакой необходимости писать класс-конфигуратор браузера, так как Selenide последних версий делает это из коробки
        Configuration.browser = Config.getProperty("browser");
        Configuration.baseUrl = "http://" + Config.getProperty("host");
        Configuration.remote = Config.getProperty("remote");
        // Проводим тестирование на фиксированном разрешении экрана
        Configuration.browserSize = "1920x1080";
        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of(
                "enableVNC", true,
                "enableLog", true
        ));
        Configuration.downloadsFolder = "target/downloads";
        Configuration.reportsFolder = "target/reports/tests";

        // Подключаем степы и скриншоты в Allure репорте
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
                .includeSelenideSteps(true));
    }

    @BeforeMethod(alwaysRun = true)
    public void handleUserSession() {
        // This runs after BaseTest's @BeforeMethod, so testData should be available
        if (testData.get() != null) {
            // Check if the current test method has @UserSession annotation
            try {
                // Get the current test method name from the stack trace
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                String testMethodName = null;
                for (StackTraceElement element : stackTrace) {
                    if (element.getClassName().contains("Test") &&
                        !element.getClassName().contains("BaseTest") &&
                        !element.getMethodName().equals("handleUserSession")) {
                        testMethodName = element.getMethodName();
                        break;
                    }
                }

                if (testMethodName != null) {
                    // Check if the test method has @UserSession annotation
                    Class<?> testClass = this.getClass();
                    try {
                        java.lang.reflect.Method testMethod = testClass.getMethod(testMethodName, String.class);
                        if (testMethod.isAnnotationPresent(UserSession.class)) {
                            logger.info("Found @UserSession annotation on method: {}", testMethodName);
                            // Create user and perform login
                            AdminSteps.createUser(testData.get().getUser());
                            LoginPage.open().login(testData.get().getUser());
                            logger.info("User created and logged in for @UserSession method: {}", testMethodName);
                        }
                    } catch (NoSuchMethodException e) {
                        // Method not found, continue without @UserSession handling
                    }
                }
            } catch (Exception e) {
                logger.warn("Error checking for @UserSession annotation: {}", e.getMessage());
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void closeWebDriver() {
        // Перезапускаем браузер после каждого теста
        Selenide.closeWebDriver();
    }

    @DataProvider(name = "browserProvider")
    protected Object[][] browserProvider() {
        return new Object[][]{{Configuration.browser}};
    }

    protected void loginAs(User user) {
        superUserRequesterWithS.getRequest(USERS).create(user);
        LoginPage.open().login(user);
    }

}
