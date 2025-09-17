package com.teamcity.ui.annotations;

import com.teamcity.ui.listeners.BrowserTestListener;
import org.testng.annotations.Listeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable browser filtering for TestNG tests.
 * This annotation automatically registers the BrowserTestListener.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Listeners(BrowserTestListener.class)
public @interface BrowserFilter {
    // This annotation serves as a marker to enable browser filtering
    // The actual browser specification is done via @Browsers annotation
}
