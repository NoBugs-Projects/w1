package com.teamcity.ui.listeners;

import com.teamcity.ui.annotations.Browsers;
import org.testng.ITestListener;
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
public class BrowserTestListener implements ITestListener {

    private static final Logger logger = Logger.getLogger(BrowserTestListener.class.getName());
    private static final String BROWSER_PROPERTY = "browser";
    private static final String DEFAULT_BROWSER = "chrome";

    @Override
    public void onTestStart(ITestResult result) {
        String configuredBrowser = getConfiguredBrowser();
        Browsers browsersAnnotation = getBrowsersAnnotation(result);

        if (browsersAnnotation == null) {
            // No @Browsers annotation, test can run on any browser
            return;
        }

        List<String> supportedBrowsers = Arrays.asList(browsersAnnotation.value());
        boolean isSupported = supportedBrowsers.stream()
                .anyMatch(browser -> browser.equalsIgnoreCase(configuredBrowser));

        if (!isSupported) {
            String supportedBrowsersList = String.join(", ", supportedBrowsers);
            String reason = String.format("Test can be run on %s browser only, but configured browser is: %s",
                supportedBrowsersList, configuredBrowser);

            logger.info(reason);
            throw new SkipException(reason);
        }
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
     * @param result the test result
     * @return the @Browsers annotation or null if not found
     */
    private Browsers getBrowsersAnnotation(ITestResult result) {
        // First check the test method
        Browsers annotation = result.getMethod().getConstructorOrMethod().getMethod()
                .getAnnotation(Browsers.class);

        if (annotation != null) {
            return annotation;
        }

        // Then check the test class
        return result.getTestClass().getRealClass().getAnnotation(Browsers.class);
    }
}
