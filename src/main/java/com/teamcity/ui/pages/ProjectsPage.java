package com.teamcity.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.teamcity.api.generators.TestDataStorage;
import io.qameta.allure.Step;

import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.teamcity.api.enums.Endpoint.PROJECTS;
import static com.teamcity.ui.Selectors.byDataTest;
import static com.teamcity.ui.Selectors.byDataTestItemtype;
import static io.qameta.allure.Allure.step;

/**
 * Page object for the TeamCity projects page.
 * <p>
 * This class represents the projects page of the TeamCity application and provides
 * methods for interacting with projects and build types. It supports project
 * verification, build execution, and ID extraction functionality.
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
 * @see TestDataStorage
 */
public class ProjectsPage extends BasePage {

    /**
     * The URL path for the projects page.
     */
    private static final String PROJECTS_URL = "/favorite/projects";

    /**
     * The expected status text for successful builds.
     */
    private static final String SUCCESS_BUILD_STATUS = "Success";

    /**
     * Main header element for page verification.
     * <p>
     * This element is used in the constructor to verify that the page has loaded completely.
     * </p>
     */
    // Везде стараемся использовать достаточно простые css селекторы.
    // Понятно, что в идеале нужно иметь атрибуты test-data на этих элементах, но здесь мы не можем на это повлиять.
    private final SelenideElement header = $(".MainPanel__router--gF > div");

    /**
     * Edit project link element for extracting project ID.
     */
    private final SelenideElement editProjectLink = $(".EditEntity__link--en");

    /**
     * Run build button element.
     */
    private final SelenideElement runButton = $(byDataTest("run-build"));

    /**
     * Build type link element.
     */
    private final SelenideElement buildType = $(".BuildTypeLine__link--MF");

    /**
     * Build type header element for verification.
     */
    private final SelenideElement buildTypeHeader = $(".BuildTypePageHeader__heading--De");

    /**
     * Build details button element.
     */
    private final SelenideElement buildDetailsButton = $(".BuildDetails__button--BC");

    /**
     * Build status link element for extracting build ID.
     */
    private final SelenideElement buildStatusLink = $(".Build__status--bG > a");

    /**
     * Collection of project elements.
     * <p>
     * This collection contains all project elements on the page, identified by
     * the data-test-itemtype attribute with value "project".
     * </p>
     */
    /* Метод Selenide.elements вызывает внутри себя этот метод $$. Официальная документация рекомендует использовать его
    для гораздо более компактной записи. */
    private final ElementsCollection projects = $$(byDataTestItemtype("project"));

    /**
     * Constructs a new ProjectsPage instance and verifies page load.
     * <p>
     * This constructor verifies that the page has loaded completely by checking
     * that the main header element is visible within the base waiting time.
     * </p>
     */
    public ProjectsPage() {
        header.shouldBe(visible, BASE_WAITING);
    }

    /**
     * Opens the projects page and returns a new ProjectsPage instance.
     * <p>
     * This static method navigates to the projects page URL and returns a new
     * ProjectsPage instance. The page constructor will automatically verify
     * that the page has loaded completely.
     * </p>
     *
     * @return a new ProjectsPage instance
     */
    @Step("Open projects page")
    /* Реализация у каждой Page статического метода open, который внутри себя вызывает конструктор класса и возвращает
    его. В конструкторе класса по умолчанию находятся ассерты, проверяющие, что страница полностью загрузилась, по этой
    причине метод open не может быть не статическим, так как невозможно создать экземпляр класса до его вызова. */
    public static ProjectsPage open() {
        return Selenide.open(PROJECTS_URL, ProjectsPage.class);
    }

    /**
     * Verifies that the specified project and build type are present and accessible.
     * <p>
     * This method finds the project by name in the projects list, clicks on it,
     * and verifies that the specified build type is present and the run button
     * is visible. It uses fluent assertions for readable test code.
     * </p>
     *
     * @param projectName the name of the project to verify
     * @param buildTypeName the name of the build type to verify
     * @return this ProjectsPage instance for method chaining
     */
    @Step("Verify project {projectName} and build type {buildTypeName}")
    public ProjectsPage verifyProjectAndBuildType(String projectName, String buildTypeName) {
        /* Найти в списке проектов элемент с нужным названием и кликнуть по нему: реализация через методы Selenide.
        Используем соответствующие Condition / CollectionCondition и should / shouldBe / shouldHave / и тд,
        чтобы код читался как красивое текстовое предложение */
        projects.findBy(exactText(projectName)).should(visible).click();
        runButton.shouldBe(visible, BASE_WAITING);
        buildType.shouldHave(exactText(buildTypeName));
        return this;
    }

    /**
     * Runs a build and waits until it completes successfully.
     * <p>
     * This method clicks on the build type, then clicks the run button, and
     * waits for the build to complete with a success status. It uses the
     * long waiting duration to accommodate build execution time.
     * </p>
     *
     * @return this ProjectsPage instance for method chaining
     */
    @Step("Run build and wait until it is finished")
    public ProjectsPage runBuildAndWaitUntilItIsFinished() {
        buildType.click();
        buildTypeHeader.should(appear, BASE_WAITING);
        runButton.click();
        buildDetailsButton.should(appear, BASE_WAITING);
        buildStatusLink.shouldHave(exactText(SUCCESS_BUILD_STATUS), LONG_WAITING);
        return this;
    }

    /**
     * Extracts the project ID from the edit project link.
     * <p>
     * This method parses the href attribute of the edit project link to extract
     * the project ID using a regular expression. The extracted ID is added to
     * the TestDataStorage for cleanup purposes.
     * </p>
     *
     * @return the extracted project ID, or null if not found
     */
    @Step("Get project id")
    // Получаем через UI айди созданного проекта
    public String getProjectId() {
        var pattern = Pattern.compile("projectId=(.*?)(?:&|$)");
        // Метод attr(text) - получить у элемента значение атрибута text
        var href = editProjectLink.attr("href");
        var matcher = href != null ? pattern.matcher(href) : null;
        var projectId = matcher != null && matcher.find() ? matcher.group(1) : null;
        step("projectId=" + projectId);
        TestDataStorage.getStorage().addCreatedEntity(PROJECTS, projectId);
        return projectId;
    }

    /**
     * Extracts the build ID from the build status link.
     * <p>
     * This method parses the href attribute of the build status link to extract
     * the build ID by taking the last part of the URL path after the final slash.
     * </p>
     *
     * @return the extracted build ID, or null if not found
     */
    @Step("Get build id")
    // Получаем через UI айди созданного билда
    public String getBuildId() {
        var href = buildStatusLink.attr("href");
        var buildId = href != null ? href.substring(href.lastIndexOf("/") + 1) : null;
        step("buildId=" + buildId);
        return buildId;
    }

}
