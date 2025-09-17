package com.teamcity.api;

import com.teamcity.api.models.BuildType;
import com.teamcity.api.models.Project;
import com.teamcity.api.models.User;
import com.teamcity.api.models.comparison.ModelAssertions;
import com.teamcity.api.requests.withS.RequesterWithS;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static io.qameta.allure.Allure.step;

import static com.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.teamcity.api.enums.Endpoint.PROJECTS;
import static com.teamcity.api.enums.Endpoint.USERS;
import static com.teamcity.api.generators.TestDataGenerator.generate;

@Feature("Search")
public class SearchTest extends BaseApiTest {

    private static final int CREATED_MODELS_COUNT = 3;
    private final RequesterWithS<Project> checkedProjectRequest = superUserRequesterWithS.getRequest(PROJECTS);
    private final RequesterWithS<User> checkedUserRequest = superUserRequesterWithS.getRequest(USERS);
    private final RequesterWithS<BuildType> checkedBuildTypeRequest = superUserRequesterWithS.getRequest(BUILD_TYPES);

    @Test(description = "User should be able to search models", groups = {"Regression"})
    public void searchTest() {
        var createdProjects = new ArrayList<Project>();
        var createdUsers = new ArrayList<User>();
        var createdBuildTypes = new ArrayList<BuildType>();

        step("Create multiple projects, users, and build types", () -> {
            for (var i = 0; i < CREATED_MODELS_COUNT; i++) {
                createdProjects.add(checkedProjectRequest.create(testData.get().getNewProjectDescription()));
                createdUsers.add(checkedUserRequest.create(testData.get().getUser()));
                createdBuildTypes.add(checkedBuildTypeRequest.create(testData.get().getBuildType()));
                testData.set(generate());
            }
        });

        var searchResults = step("Search for all created entities", () -> {
            var projects = checkedProjectRequest.search();
            var users = checkedUserRequest.search();
            var buildTypes = checkedBuildTypeRequest.search();
            return new SearchResults(projects, users, buildTypes);
        });

        step("Verify all created projects are found in search results", () -> {
            for (var createdProject : createdProjects) {
                var foundProject = searchResults.projects().stream()
                        .filter(p -> p.getId().equals(createdProject.getId()))
                        .findFirst()
                        .orElse(null);
                ModelAssertions.assertThatModels(createdProject, foundProject).match();
            }
        });

        step("Verify all created users are found in search results", () -> {
            for (var createdUser : createdUsers) {
                var foundUser = searchResults.users().stream()
                        .filter(u -> u.getId().equals(createdUser.getId()))
                        .findFirst()
                        .orElse(null);
                ModelAssertions.assertThatModels(createdUser, foundUser).match();
            }
        });

        step("Verify all created build types are found in search results", () -> {
            for (var createdBuildType : createdBuildTypes) {
                var foundBuildType = searchResults.buildTypes().stream()
                        .filter(bt -> bt.getId().equals(createdBuildType.getId()))
                        .findFirst()
                        .orElse(null);
                ModelAssertions.assertThatModels(createdBuildType, foundBuildType).match();
            }
        });
    }

    // Helper record to hold search results
    private record SearchResults(
            java.util.List<Project> projects,
            java.util.List<User> users,
            java.util.List<BuildType> buildTypes
    ) {}

}
