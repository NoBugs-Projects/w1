package com.teamcity.api.enums;

/**
 * Enumeration of user roles in the TeamCity system.
 * <p>
 * This enum defines the different permission levels and roles that can be assigned
 * to users in the TeamCity system. Each role provides different levels of access
 * to various system resources and operations.
 * </p>
 *
 * <p>
 * The enum constants are defined without additional parameters since the same
 * result can be obtained using the .toString() method, which returns the
 * constant name as a string.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 */
public enum UserRole {

    // Убираем параметр с указанием "SYSTEM_ADMIN" и т.д., так как тот же результат можно получить методом .toString()

    /**
     * System administrator role.
     * <p>
     * Users with this role have full administrative access to the entire TeamCity
     * system, including all projects, build configurations, and system settings.
     * </p>
     */
    SYSTEM_ADMIN,

    /**
     * Project administrator role.
     * <p>
     * Users with this role have administrative access to specific projects,
     * including the ability to create and manage build configurations within
     * those projects.
     * </p>
     */
    PROJECT_ADMIN,

    /**
     * Project developer role.
     * <p>
     * Users with this role have developer-level access to specific projects,
     * including the ability to trigger builds and view build results, but
     * cannot modify project settings or build configurations.
     * </p>
     */
    PROJECT_DEVELOPER,

    /**
     * Project viewer role.
     * <p>
     * Users with this role have read-only access to specific projects,
     * including the ability to view project information and build results,
     * but cannot trigger builds or modify any project settings.
     * </p>
     */
    PROJECT_VIEWER,

    /**
     * Agent manager role.
     * <p>
     * Users with this role have administrative access to build agents,
     * including the ability to manage agent configurations and monitor
     * agent status across the system.
     * </p>
     */
    AGENT_MANAGER

}
