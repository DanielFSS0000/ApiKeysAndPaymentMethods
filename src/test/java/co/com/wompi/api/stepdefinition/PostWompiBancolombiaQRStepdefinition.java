package co.com.wompi.api.stepdefinition;

import co.com.wompi.api.models.BancolombiaQRTransactionRequest;
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
public class PostWompiBancolombiaQRStepdefinition {
    private EnvironmentVariables environmentVariables;

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @When("the user creates a Bancolombia QR transaction in Wompi")
    public void userCreatesBancolombiaQRTransaction() {

        String wompiBaseUrlKey = Constants.BASE_URL.replace(Constants.TYPE_ENVIRONMENT, "sandbox");
        String wompiBaseUrl = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty(wompiBaseUrlKey);
        String token = AcceptanceTokenFetcher.fetch(environmentVariables);
        BancolombiaQRTransactionRequest req = TransactionRequestBuilder.bancolombiaQr(
                token,
                "pruebassandboxdaniel@yopmail.com",
                "Pago a Tienda Wompi",
                "APPROVED",
                1000000,
                "COP"
        );

        OnStage.theActorCalled(Constants.ACTOR).attemptsTo(
                CreateTransaction.with(req, wompiBaseUrl, "transactions", Constants.PRIVATE_KEY_WOMPI)
        );
    }

    @Then("the QR response status should be {string}")
    public void theResponseStatusShouldBe(String expectedStatus) {
        OnStage.theActorInTheSpotlight().should(
                seeThat(TransactionStatus.at("data.status"), equalTo(expectedStatus))
        );
    }

    @Then("after checking the transaction by ID, the status should be {string}")
    public void theTransactionStatusShouldBe(String expectedStatus) throws InterruptedException {

        String transactionId = SerenityRest.lastResponse().jsonPath().getString("data.id");

        Thread.sleep(2000);

        String wompiBaseUrlKey = Constants.BASE_URL.replace(Constants.TYPE_ENVIRONMENT, "sandbox");
        String wompiBaseUrl = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty(wompiBaseUrlKey);

        SerenityRest
                .given()
                .header("Authorization", "Bearer " + Constants.PRIVATE_KEY_WOMPI)
                .get(wompiBaseUrl + "transactions/" + transactionId)
                .then()
                .statusCode(200);

        String sandboxId = SerenityRest.lastResponse().jsonPath().getString("data.id");
        String sandboxStatus = SerenityRest.lastResponse().jsonPath()
                .getString("data.payment_method.sandbox_status");

        log.info("BancolombiaQR ID Transaction: {} ", sandboxId);
        log.info("BancolombiaQR Status: {} " , sandboxStatus);

        OnStage.theActorInTheSpotlight().should(
                seeThat(
                        actor -> SerenityRest.lastResponse().jsonPath().getString("data.status"),
                        equalTo(expectedStatus)
                )
        );
    }

    @Then("the payment method QR should be {string}")
    public void thePaymentMethodShouldBe(String method) {
        OnStage.theActorInTheSpotlight().should(
                seeThat(
                        actor -> SerenityRest.lastResponse().jsonPath()
                                .getString("data.payment_method.type"),
                        equalTo(method)
                )
        );
    }
}