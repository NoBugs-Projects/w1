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

public class ProjectsPage extends BasePage {

    private static final String PROJECTS_URL = "/favorite/projects";
    private static final String SUCCESS_BUILD_STATUS = "Success";
    // Везде стараемся использовать достаточно простые css селекторы.
    // Понятно, что в идеале нужно иметь атрибуты test-data на этих элементах, но здесь мы не можем на это повлиять.
    private final SelenideElement header = $(".MainPanel__router--gF > div");
    private final SelenideElement editProjectLink = $(".EditEntity__link--en");
    private final SelenideElement runButton = $(byDataTest("run-build"));
    private final SelenideElement buildType = $(".BuildTypeLine__link--MF");
    private final SelenideElement buildTypeHeader = $(".BuildTypePageHeader__heading--De");
    private final SelenideElement buildDetailsButton = $(".BuildDetails__button--BC");
    private final SelenideElement buildStatusLink = $(".Build__status--bG > a");
    /* Метод Selenide.elements вызывает внутри себя этот метод $$. Официальная документация рекомендует использовать его
    для гораздо более компактной записи. */
    private final ElementsCollection projects = $$(byDataTestItemtype("project"));

    public ProjectsPage() {
        header.shouldBe(visible, BASE_WAITING);
    }

    @Step("Open projects page")
    /* Реализация у каждой Page статического метода open, который внутри себя вызывает конструктор класса и возвращает
    его. В конструкторе класса по умолчанию находятся ассерты, проверяющие, что страница полностью загрузилась, по этой
    причине метод open не может быть не статическим, так как невозможно создать экземпляр класса до его вызова. */
    public static ProjectsPage open() {
        return Selenide.open(PROJECTS_URL, ProjectsPage.class);
    }

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

    @Step("Run build and wait until it is finished")
    public ProjectsPage runBuildAndWaitUntilItIsFinished() {
        buildType.click();
        buildTypeHeader.should(appear, BASE_WAITING);
        runButton.click();
        buildDetailsButton.should(appear, BASE_WAITING);
        buildStatusLink.shouldHave(exactText(SUCCESS_BUILD_STATUS), LONG_WAITING);
        return this;
    }

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

    @Step("Get build id")
    // Получаем через UI айди созданного билда
    public String getBuildId() {
        var href = buildStatusLink.attr("href");
        var buildId = href != null ? href.substring(href.lastIndexOf("/") + 1) : null;
        step("buildId=" + buildId);
        return buildId;
    }

}
