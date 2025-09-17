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

/**
 * Page object for the TeamCity build type edit page.
 * <p>
 * This class represents the build type edit page in the TeamCity admin interface
 * and provides methods for editing build type configurations and extracting
 * build type IDs from the current URL.
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
public class EditBuildTypePage extends BasePage {

    /**
     * General tab element for page verification.
     * <p>
     * This element is used to verify that the page has loaded completely.
     * </p>
     */
    private final SelenideElement generalTab = $("#general_Tab");
    
    /**
     * Header help icon element for page verification.
     * <p>
     * This element is used as an additional indicator that the page has loaded
     * completely. It is present in all states of the build type edit page,
     * regardless of the source or configuration.
     * </p>
     * 
     * <p>
     * The element is located using a complex CSS selector that targets a span
     * element that is the first child of a div, which is a sibling of an h2 element.
     * </p>
     */
    /* При открытии страницы с редактированием билд конфигурации открываемая страница может выглядеть по-разному.
    Например, страница открывается после создания проекта через UI, причем ее состояние может быть разным, в зависимости
    от указанного git url. А если зайти в редактирование билд конфигурации у ранее созданного проекта, то ее состояние
    тоже будет другим. Поэтому необходимо было завязаться на элемент-индикатор того, что страница полностью загрузилась,
    который присутствует в любом состоянии страницы.
    Ищем элемент span, который является первым дочерним элементом div, который, в свою очередь, является sibling у h2 */
    private final SelenideElement headerHelpIcon = $("h2 + div > span");

    /**
     * Constructs a new EditBuildTypePage instance and verifies page load.
     * <p>
     * This constructor verifies that the page has loaded completely by checking
     * that both the general tab and header help icon elements are visible
     * within the base waiting time.
     * </p>
     */
    public EditBuildTypePage() {
        generalTab.shouldBe(visible, BASE_WAITING);
        headerHelpIcon.shouldBe(visible, BASE_WAITING);
    }

    /**
     * Opens the build type edit page and returns a new EditBuildTypePage instance.
     * <p>
     * This static method creates a new EditBuildTypePage instance using the
     * current page context. It is typically used when navigating from another
     * page that has already loaded the edit page.
     * </p>
     * 
     * @return a new EditBuildTypePage instance
     */
    @Step("Open build type edit page")
    public static EditBuildTypePage open() {
        return page(EditBuildTypePage.class);
    }

    /**
     * Extracts the build type ID from the current URL.
     * <p>
     * This method parses the current URL to extract the build type ID using
     * a regular expression. The extracted ID is added to the TestDataStorage
     * for cleanup purposes.
     * </p>
     * 
     * @return the extracted build type ID, or null if not found
     */
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
