
package co.com.wompi.api.stepdefinition;

import co.com.wompi.api.models.NequiTransactionRequest;
import co.com.wompi.api.questions.TransactionStatus;
import co.com.wompi.api.tasks.post.CreateTransaction;
import co.com.wompi.api.utils.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.thucydides.core.util.EnvironmentVariables;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
public class PostWompiNequiStepdefinition {
    private EnvironmentVariables environmentVariables;

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @When("the user creates a NEQUI transaction in Wompi")
    public void createNequiTransaction() {
        String wompiBaseUrlKey = Constants.BASE_URL.replace(Constants.TYPE_ENVIRONMENT, "sandbox");
        String wompiBaseUrl = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty(wompiBaseUrlKey);

        String token = AcceptanceTokenFetcher.fetch(environmentVariables);

        NequiTransactionRequest req = TransactionRequestBuilder.nequi(
                token,
                "pruebassandboxdaniel@yopmail.com",
                "3991111111",
                1000000,
                "COP"
        );

        log.info("[Debug] Cadena para firmar: {}:{}:{};{} " , req.reference , req.amount_in_cents, req.currency,
                Constants.INTEGRITY_KEY_WOMPI);

        OnStage.theActorCalled(Constants.ACTOR).attemptsTo(
                CreateTransaction.with(req, wompiBaseUrl, "transactions", Constants.PRIVATE_KEY_WOMPI)
        );

    }

    @Then("the response status should be {string}")
    public void responseStatusShouldPENDING(String expectedStatus) {
        OnStage.theActorInTheSpotlight().should(
                seeThat(TransactionStatus.at("data.status"), equalTo(expectedStatus))
        );
    }

    @Then("after a few minutes the user validates the {string} status")
    public void theTransactionIsApproved(String expectedStatus) throws InterruptedException {
        String transactionId = SerenityRest.lastResponse().jsonPath().getString("data.id");

        Thread.sleep(7000);
        String wompiBaseUrlKey = Constants.BASE_URL.replace(Constants.TYPE_ENVIRONMENT, "sandbox");
        String wompiBaseUrl = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty(wompiBaseUrlKey);

        SerenityRest
                .given()
                .header("Authorization", "Bearer " + Constants.PRIVATE_KEY_WOMPI)
                .get(wompiBaseUrl + "transactions/" + transactionId)
                .then()
                .statusCode(200);

        System.out.println("=== ESTADO FINAL DE LA TRANSACCIÓN NEQUI ===");
        String sandboxId = SerenityRest.lastResponse().jsonPath().getString("data.id");
        String sandboxStatus = SerenityRest.lastResponse().jsonPath().getString("data.status");
        log.info("Nequi Id Transaction: {} " , sandboxId);
        log.info("Nequi Status: {} ", sandboxStatus);

        OnStage.theActorInTheSpotlight().should(
                seeThat(
                        actor -> SerenityRest.lastResponse().jsonPath().getString("data.status"),
                        equalTo(expectedStatus)
                )
        );
    }

    @Then("the payment method should be {string}")
    public void validNequiPaymentMethod(String expectedMethod) {
        OnStage.theActorInTheSpotlight().should(
                seeThat(
                        actor -> SerenityRest.lastResponse()
                                .jsonPath()
                                .getString("data.payment_method.type"),
                        equalTo(expectedMethod)
                )
        );
    }
}