package co.com.wompi.api.stepdefinition;

import co.com.wompi.api.models.PseTransactionRequest;
import co.com.wompi.api.questions.TransactionStatus;
import co.com.wompi.api.tasks.post.CreateTransaction;
import co.com.wompi.api.utils.AcceptanceTokenFetcher;
import co.com.wompi.api.utils.Constants;
import co.com.wompi.api.utils.TransactionRequestBuilder;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.thucydides.core.util.EnvironmentVariables;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.equalTo;

public class PostWompiPseStepdefinition {

    private EnvironmentVariables environmentVariables;

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @When("the user creates a PSE transaction in Wompi with approving bank")
    public void userCreatesPseTransactionApprovingBank() {

        String wompiBaseUrlKey = Constants.BASE_URL.replace(Constants.TYPE_ENVIRONMENT, "sandbox");
        String wompiBaseUrl = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty(wompiBaseUrlKey);

        String token = AcceptanceTokenFetcher.fetch(environmentVariables);

        PseTransactionRequest req = TransactionRequestBuilder.pse(
                token,
                "pruebassandboxdaniel@yopmail.com",
                1000000,
                "COP",
                0,
                "CC",
                "1999888777",
                "2",
                "Pago Tienda Wompi"
        );

        OnStage.theActorCalled(Constants.ACTOR).attemptsTo(
                CreateTransaction.with(req, wompiBaseUrl, "transactions", Constants.PRIVATE_KEY_WOMPI)
        );
    }

    @Then("the PSE response status should be {string}")
    public void theResponseStatusShouldBePending(String expectedStatus) {
        OnStage.theActorInTheSpotlight().should(
                seeThat(TransactionStatus.at("data.status"), equalTo(expectedStatus))
        );
    }

    @Then("the user waits a few minutes to validate the {string} status")
    public void theTransactionPseIsDeclined(String expectedStatus) throws InterruptedException {
        String transactionId = SerenityRest.lastResponse().jsonPath().getString("data.id");

        Thread.sleep(6000);

        String wompiBaseUrlKey = Constants.BASE_URL.replace(Constants.TYPE_ENVIRONMENT, "sandbox");
        String wompiBaseUrl = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty(wompiBaseUrlKey);

        SerenityRest
                .given()
                .header("Authorization", "Bearer " + Constants.PRIVATE_KEY_WOMPI)
                .get(wompiBaseUrl + "transactions/" + transactionId)
                .then()
                .statusCode(200);

        System.out.println("=== ESTADO DE LA TRANSACCIÓN PSE ===");
        String sandboxId = SerenityRest.lastResponse().jsonPath().getString("data.id");
        String sandboxStatus = SerenityRest.lastResponse().jsonPath().getString("data.status");
        System.out.println("PSE Id Transaction: " + sandboxId);
        System.out.println("PSE Status: " + sandboxStatus);

        OnStage.theActorInTheSpotlight().should(
                seeThat(
                        actor -> SerenityRest.lastResponse().jsonPath().getString("data.status"),
                        equalTo(expectedStatus)
                )
        );
    }

    @Then("the payment method used is {string}")
    public void thePaymentMethodShouldBePSE(String expectedMethod) {
        OnStage.theActorInTheSpotlight().should(
                seeThat(
                        actor -> SerenityRest.lastResponse().jsonPath()
                                .getString("data.payment_method.type"),
                        equalTo(expectedMethod)
                )
        );
    }
}