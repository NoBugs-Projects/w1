package com.teamcity.api.models.comparison;

/**
 * Fluent assertion class for comparing model objects.
 * <p>
 * This class provides a fluent API for comparing request and response model objects
 * based on configurable field mappings. It uses the ModelComparisonConfigLoader to
 * determine which fields should be compared and how they should be mapped.
 * </p>
 *
 * <p>
 * The comparison is performed using reflection to access field values and compare
 * them according to the rules defined in the model-comparison.properties file.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see ModelComparisonConfigLoader
 * @see ModelComparator
 */
public final class ModelAssertions {

    /**
     * The request model object to compare.
     */
    private final Object request;

    /**
     * The response model object to compare against.
     */
    private final Object response;

    /**
     * Private constructor to prevent direct instantiation.
     * <p>
     * Use the static factory method assertThatModels() to create instances.
     * </p>
     *
     * @param request the request model object
     * @param response the response model object
     */
    private ModelAssertions(Object request, Object response) {
        this.request = request;
        this.response = response;
    }

    /**
     * Creates a new ModelAssertions instance for the given request and response objects.
     * <p>
     * This is the entry point for model comparison assertions. It returns a fluent
     * API that can be chained with the match() method to perform the comparison.
     * </p>
     *
     * @param request the request model object to compare
     * @param response the response model object to compare against
     * @return a new ModelAssertions instance
     */
    public static ModelAssertions assertThatModels(Object request, Object response) {
        return new ModelAssertions(request, response);
    }

    /**
     * Performs the model comparison and throws an AssertionError if fields don't match.
     * <p>
     * This method loads the comparison configuration, finds the appropriate rule
     * for the request class, and compares the specified fields. If any fields
     * don't match, an AssertionError is thrown with detailed information about
     * the mismatches.
     * </p>
     *
     * @return this ModelAssertions instance for method chaining
     * @throws AssertionError if the model comparison fails or no rule is found
     */
    public ModelAssertions match() {
        ModelComparisonConfigLoader configLoader = new ModelComparisonConfigLoader("model-comparison.properties");
        ModelComparisonConfigLoader.ComparisonRule rule = configLoader.getRuleFor(request.getClass());

        if (rule != null) {
            ModelComparator.ComparisonResult result = ModelComparator.compareFields(
                    request,
                    response,
                    rule.getFieldMappings()
            );

            if (!result.isSuccess()) {
                throw new AssertionError("Model comparison failed with mismatched fields:\n" + result);
            }
        } else {
            throw new AssertionError("No comparison rule found for class " + request.getClass().getSimpleName());
        }

        return this;
    }
}
