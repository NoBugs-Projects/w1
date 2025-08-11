package com.teamcity.ui;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byCssSelector;

public final class Selectors {

    private Selectors() {
    }

    /* Оператор ~= ищет частичное вхождение. Например, byDataTest("runner-item") найдет элемент с атрибутом
    data-test="runner-item simpleRunner".
    От остальных кастомных By методов было решено отказаться, так как те же byId и byClassName можно реализовать
    короче с помощью css селектора */
    public static By byDataTest(String dataTest) {
        return byCssSelector("[data-test~='%s']".formatted(dataTest));
    }

    public static By byDataTestItemtype(String dataTestItemtype) {
        return byAttribute("data-test-itemtype", dataTestItemtype);
    }

}
