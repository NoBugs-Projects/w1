package com.teamcity.ui;

import com.teamcity.ui.pages.setup.FirstStartPage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

@Feature("Setup")
public class SetupServerTest extends BaseUiTest {

    @Test(groups = {"Setup"})
    public void setupTeamCityServerTest(String ignoredBrowser) {
        FirstStartPage.open().setupFirstStart();
    }

}
