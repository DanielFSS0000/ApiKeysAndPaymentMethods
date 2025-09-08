package co.com.wompi.api.utils;


import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.util.EnvironmentVariables;

public final class AcceptanceTokenFetcher {

    private AcceptanceTokenFetcher() {
    }

    public static String fetch(EnvironmentVariables environmentVariables) {
        String wompiBaseUrlKey = Constants.BASE_URL.replace(Constants.TYPE_ENVIRONMENT, "sandbox");
        String baseUrl = EnvironmentSpecificConfiguration
                .from(environmentVariables)
                .getProperty(wompiBaseUrlKey);
        return SerenityRest
                .given()
                .get(baseUrl + "merchants/" + Constants.PUBLIC_KEY_WOMPI)
                .jsonPath()
                .getString("data.presigned_acceptance.acceptance_token");

    }
}
