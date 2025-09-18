package com.teamcity.ui.listeners;

import com.teamcity.ui.annotations.Browsers;
import org.testng.IInvokedMethodListener;
import org.testng.IInvokedMethod;
import org.testng.ITestResult;
import org.testng.SkipException;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * TestNG listener that filters tests based on browser compatibility.
 * Tests annotated with @Browsers will only run if the configured browser
 * matches one of the specified browsers.
 */
public class BrowserTestListener implements IInvokedMethodListener {

    private static final Logger logger = Logger.getLogger(BrowserTestListener.class.getName());
    private static final String BROWSER_PROPERTY = "browser";
    private static final String DEFAULT_BROWSER = "chrome";

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        String configuredBrowser = getConfiguredBrowser();
        Browsers browsersAnnotation = getBrowsersAnnotation(method, testResult);

        if (browsersAnnotation == null) {
            // No @Browsers annotation, test can run on any browser
            logger.info("No @Browsers annotation found for method: " + method.getTestMethod().getMethodName() + ". Test will run.");
            return;
        }

        List<String> supportedBrowsers = Arrays.asList(browsersAnnotation.value());
        boolean isSupported = supportedBrowsers.stream()
                .anyMatch(browser -> browser.equalsIgnoreCase(configuredBrowser));

        if (!isSupported) {
            String supportedBrowsersList = String.join(", ", supportedBrowsers);
            String reason = String.format("Test can be run on %s browser(s) only, but configured browser is: %s. Skipping test: %s",
                supportedBrowsersList, configuredBrowser, method.getTestMethod().getMethodName());

            logger.warning(reason);
            throw new SkipException(reason);
        } else {
            logger.info(String.format("Configured browser '%s' is supported for test: %s. Test will run.",
                configuredBrowser, method.getTestMethod().getMethodName()));
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        // No action needed after invocation for this listener
    }

    /**
     * Gets the configured browser from system properties or config file.
     *
     * @return the configured browser name
     */
    private String getConfiguredBrowser() {
        // First try system property
        String browser = System.getProperty(BROWSER_PROPERTY);
        if (browser != null && !browser.trim().isEmpty()) {
            return browser.trim().toLowerCase();
        }

        // Then try from config.properties
        try {
            Properties config = new Properties();
            config.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            browser = config.getProperty(BROWSER_PROPERTY);
            if (browser != null && !browser.trim().isEmpty()) {
                return browser.trim().toLowerCase();
            }
        } catch (Exception e) {
            logger.warning("Could not load config.properties: " + e.getMessage());
        }

        // Default to chrome if nothing is configured
        return DEFAULT_BROWSER;
    }

    /**
     * Gets the @Browsers annotation from the test method or class.
     *
     * @param method the invoked method
     * @param testResult the test result
     * @return the @Browsers annotation or null if not found
     */
    private Browsers getBrowsersAnnotation(IInvokedMethod method, ITestResult testResult) {
        // First check the test method
        Browsers annotation = method.getTestMethod().getConstructorOrMethod().getMethod()
                .getAnnotation(Browsers.class);

        if (annotation != null) {
            return annotation;
        }

        // Then check the test class
        return testResult.getTestClass().getRealClass().getAnnotation(Browsers.class);
    }
}
