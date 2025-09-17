package com.teamcity.ui;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byCssSelector;

/**
 * Utility class for creating custom Selenium selectors.
 * <p>
 * This class provides static methods for creating custom By selectors that are
 * commonly used in the TeamCity UI testing framework. It includes selectors
 * for data-test attributes and other custom attributes specific to the
 * TeamCity application.
 * </p>
 *
 * <p>
 * The class uses CSS selectors and attribute selectors to provide efficient
 * and readable element location strategies for UI automation.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see By
 */
public final class Selectors {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Selectors() {
    }

    /**
     * Creates a By selector for elements with a specific data-test attribute value.
     * <p>
     * This method creates a CSS selector that uses the ~= operator to find elements
     * with a data-test attribute that contains the specified value as a word.
     * For example, byDataTest("runner-item") will find elements with
     * data-test="runner-item simpleRunner".
     * </p>
     *
     * <p>
     * The ~= operator searches for partial matches, making it useful for finding
     * elements that have multiple values in their data-test attribute.
     * </p>
     *
     * @param dataTest the data-test attribute value to search for
     * @return a By selector for elements with the specified data-test value
     */
    /* Оператор ~= ищет частичное вхождение. Например, byDataTest("runner-item") найдет элемент с атрибутом
    data-test="runner-item simpleRunner".
    От остальных кастомных By методов было решено отказаться, так как те же byId и byClassName можно реализовать
    короче с помощью css селектора */
    public static By byDataTest(String dataTest) {
        return byCssSelector("[data-test~='%s']".formatted(dataTest));
    }

    /**
     * Creates a By selector for elements with a specific data-test-itemtype attribute value.
     * <p>
     * This method creates an attribute selector that finds elements with a
     * data-test-itemtype attribute matching the specified value exactly.
     * This is useful for finding elements that represent specific types of
     * items in the TeamCity UI.
     * </p>
     *
     * @param dataTestItemtype the data-test-itemtype attribute value to search for
     * @return a By selector for elements with the specified data-test-itemtype value
     */
    public static By byDataTestItemtype(String dataTestItemtype) {
        return byAttribute("data-test-itemtype", dataTestItemtype);
    }

}
