package com.teamcity.api;

import com.teamcity.api.annotations.ManualTest;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

@Feature("Performance")
public class PerformanceTest extends BaseApiTest {

    @Test(description = "Fast test for baseline performance measurement", groups = {"Performance"})
    public void fastTest() {
        // Simulate fast test execution
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test(description = "Medium duration test for performance analysis", groups = {"Performance"})
    public void mediumTest() {
        // Simulate medium test execution
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test(description = "Slow test to identify bottlenecks", groups = {"Performance"})
    public void slowTest() {
        // Simulate slow test execution
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test(description = "Very slow test for bottleneck detection", groups = {"Performance"})
    public void verySlowTest() {
        // Simulate very slow test execution
        try {
            TimeUnit.MILLISECONDS.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test(description = "Test with similar pattern for redundancy detection", groups = {"Performance"})
    public void similarPatternTest1() {
        // Simulate test with similar pattern
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test(description = "Test with similar pattern for redundancy detection", groups = {"Performance"})
    public void similarPatternTest2() {
        // Simulate test with similar pattern
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test(description = "Test with similar pattern for redundancy detection", groups = {"Performance"})
    public void similarPatternTest3() {
        // Simulate test with similar pattern
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
