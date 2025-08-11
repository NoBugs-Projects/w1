package com.teamcity.ui.pages.admin;

import com.codeborne.selenide.SelenideElement;
import com.teamcity.api.generators.TestDataStorage;
import com.teamcity.ui.pages.BasePage;
import io.qameta.allure.Step;

import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static io.qameta.allure.Allure.step;

public class EditBuildTypePage extends BasePage {

    private final SelenideElement generalTab = $("#general_Tab");
    /* При открытии страницы с редактированием билд конфигурации открываемая страница может выглядеть по-разному.
    Например, страница открывается после создания проекта через UI, причем ее состояние может быть разным, в зависимости
    от указанного git url. А если зайти в редактирование билд конфигурации у ранее созданного проекта, то ее состояние
    тоже будет другим. Поэтому необходимо было завязаться на элемент-индикатор того, что страница полностью загрузилась,
    который присутствует в любом состоянии страницы.
    Ищем элемент span, который является первым дочерним элементом div, который, в свою очередь, является sibling у h2 */
    private final SelenideElement headerHelpIcon = $("h2 + div > span");

    public EditBuildTypePage() {
        generalTab.shouldBe(visible, BASE_WAITING);
        headerHelpIcon.shouldBe(visible, BASE_WAITING);
    }

    @Step("Open build type edit page")
    public static EditBuildTypePage open() {
        return page(EditBuildTypePage.class);
    }

    @Step("Get build type id")
    // Получаем через UI айди созданной билд конфигурации
    public String getBuildTypeId() {
        var pattern = Pattern.compile("buildType:(.*?)(?:&|$)");
        var matcher = pattern.matcher(url());
        var buildTypeId = matcher.find() ? matcher.group(1) : null;
        step("buildTypeId=" + buildTypeId);
        TestDataStorage.getStorage().addCreatedEntity(BUILD_TYPES, buildTypeId);
        return buildTypeId;
    }

}
