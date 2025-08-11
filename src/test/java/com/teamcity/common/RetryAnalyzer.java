package com.teamcity.common;

import com.teamcity.api.config.Config;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public final class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    @Override
    // Механизм ретрая теста в случае падения
    public boolean retry(ITestResult result) {
        if (retryCount < Integer.parseInt(Config.getProperty("maxRetryCount"))) {
            retryCount++;
            return true;
        }
        return false;
    }

}
