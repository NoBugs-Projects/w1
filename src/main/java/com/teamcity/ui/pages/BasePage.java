package com.teamcity.ui.pages;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;

/**
 * Abstract base class for all UI page objects.
 * <p>
 * This class provides common functionality and shared elements that are used
 * across multiple page objects in the TeamCity UI testing framework. It includes
 * common waiting durations, shared UI elements, and other utilities that help
 * maintain consistency across page implementations.
 * </p>
 *
 * <p>
 * The class uses Selenide for web element interaction and provides a foundation
 * for implementing the Page Object Model pattern in UI tests.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see SelenideElement
 */
public abstract class BasePage {

    /**
     * Base waiting duration for standard UI operations.
     * <p>
     * This duration is used for most UI interactions that require waiting
     * for elements to appear or become interactive.
     * </p>
     */
    protected static final Duration BASE_WAITING = Duration.ofSeconds(30);

    /**
     * Long waiting duration for operations that may take more time.
     * <p>
     * This duration is used for operations that typically require more time
     * to complete, such as build processes or complex UI updates.
     * </p>
     */
    protected static final Duration LONG_WAITING = Duration.ofMinutes(3);

    /**
     * Common submit button element used across multiple pages.
     * <p>
     * This element represents the submit button that is commonly found in
     * form pages throughout the TeamCity UI. The CSS selector targets the
     * submit button that is the first child of an element with class
     * "saveButtonsBlock".
     * </p>
     *
     * <p>
     * The Selenide.element method internally calls the $ method, and the
     * official documentation recommends using $ for more compact notation.
     * </p>
     */
    /* Метод Selenide.element вызывает внутри себя этот метод $. Официальная документация рекомендует использовать его
    для гораздо более компактной записи.
    Здесь используется css селектор: элемент с классом submitButton, который является первым дочерним элементом
    у элемента с классом saveButtonsBlock */
    protected final SelenideElement submitButton = $(".saveButtonsBlock > .submitButton");

}
