package co.com.wompi.api.stepdefinition;

import co.com.wompi.api.models.PcolTransactionRequest;
import co.com.wompi.api.tasks.post.CreateTransaction;
import co.com.wompi.api.utils.AcceptanceTokenFetcher;
import co.com.wompi.api.utils.Constants;
import co.com.wompi.api.utils.ReferenceGenerator;
import co.com.wompi.api.utils.SignatureUtils;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.thucydides.core.util.EnvironmentVariables;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PostWompiPcolStepdefinition {
    private EnvironmentVariables environmentVariables;

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @When("the user creates a PCOL transaction in Wompi with APPROVED_ONLY_POINTS status")
    public void userCreatesPcolTransaction() {
        String wompiBaseUrlKey = Constants.BASE_URL.replace(Constants.TYPE_ENVIRONMENT, "sandbox");
        String wompiBaseUrl = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty(wompiBaseUrlKey);

        String token = AcceptanceTokenFetcher.fetch(environmentVariables);

        PcolTransactionRequest req = new PcolTransactionRequest();
        req.amount_in_cents = 1000000;
        req.currency = "COP";
        req.customer_email = "pruebasensandbox@yopmail.com";
        req.reference = ReferenceGenerator.randomNequiReference();
        req.acceptance_token = token;
        req.payment_method = new PcolTransactionRequest.PaymentMethod();
        req.payment_method.sandbox_status = "APPROVED_ONLY_POINTS";

        req.signature = SignatureUtils.calcularFirmaIntegridad(
                req.reference,
                req.amount_in_cents,
                req.currency,
                Constants.INTEGRITY_KEY_WOMPI
        );

        OnStage.theActorCalled(Constants.ACTOR).attemptsTo(
                CreateTransaction.with(req, wompiBaseUrl, "transactions", Constants.PRIVATE_KEY_WOMPI)
        );
    }

    @Then("the response error type should be {string}")
    public void theResponseErrorTypeShouldBe(String expectedType) {
        String actualType = SerenityRest.lastResponse().jsonPath().getString("error.type");
        assertThat(actualType, equalTo(expectedType));
    }

    @Then("the error reason should be {string}")
    public void theErrorReasonShouldBe(String expectedReason) {
        String actualReason = SerenityRest.lastResponse().jsonPath().getString("error.reason");
        assertThat(actualReason, equalTo(expectedReason));
    }
}