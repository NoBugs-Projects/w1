package com.teamcity.api;

import com.teamcity.BaseTest;
import com.teamcity.api.models.AuthModules;
import com.teamcity.api.models.ServerAuthSettings;
import com.teamcity.api.requests.withS.ServerAuthSettingsRequesterWithS;
import com.teamcity.api.spec.RequestSpecs;
import io.qameta.allure.awaitility.AllureAwaitilityListener;
import org.awaitility.Awaitility;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;

import static com.teamcity.api.generators.TestDataGenerator.generate;

public abstract class BaseApiTest extends BaseTest {

    private final ServerAuthSettingsRequesterWithS serverAuthSettingsRequesterWithSRequest = new ServerAuthSettingsRequesterWithS(RequestSpecs.superUserSpec());
    private AuthModules authModules;
    private boolean perProjectPermissions;

    @BeforeSuite(alwaysRun = true)
    public void setUpServerAuthSettings() {
        // Отображение Awaitility действий в Allure репорте, настройка Awaitility
        Awaitility.setDefaultConditionEvaluationListener(new AllureAwaitilityListener());
        Awaitility.setDefaultPollInterval(Duration.ofSeconds(3));
        Awaitility.setDefaultTimeout(Duration.ofSeconds(30));
        Awaitility.pollInSameThread();

        // Получаем текущее значение настройки perProjectPermissions
        perProjectPermissions = serverAuthSettingsRequesterWithSRequest.read(null)
                .getPerProjectPermissions();

        authModules = generate(AuthModules.class);
        // Обновляем значение настройки perProjectPermissions на true (для тестирования ролей)
        serverAuthSettingsRequesterWithSRequest.update(null, ServerAuthSettings.builder()
                .perProjectPermissions(true)
                .modules(authModules)
                .build());
    }

    @AfterSuite(alwaysRun = true)
    public void cleanUpServerAuthSettings() {
        // Возвращаем настройке perProjectPermissions исходное значение, которые было перед запуском тестов
        serverAuthSettingsRequesterWithSRequest.update(null, ServerAuthSettings.builder()
                .perProjectPermissions(perProjectPermissions)
                .modules(authModules)
                .build());
    }

}
