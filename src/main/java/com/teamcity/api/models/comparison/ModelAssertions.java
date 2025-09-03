package com.teamcity.api.models.comparison;

public final class ModelAssertions {

    private final Object request;
    private final Object response;

    private ModelAssertions(Object request, Object response) {
        this.request = request;
        this.response = response;
    }

    public static ModelAssertions assertThatModels(Object request, Object response) {
        return new ModelAssertions(request, response);
    }

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
