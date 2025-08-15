package co.com.wompi.api.stepdefinition;

import co.com.wompi.api.models.BancolombiaQRTransactionRequest;
import co.com.wompi.api.tasks.post.CreateTransaction;
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

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.equalTo;

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

        String acceptanceToken = SerenityRest
                .given()
                .get(wompiBaseUrl + "merchants/" + Constants.PUBLIC_KEY_WOMPI)
                .jsonPath()
                .getString("data.presigned_acceptance.acceptance_token");

        BancolombiaQRTransactionRequest req = new BancolombiaQRTransactionRequest();
        req.amount_in_cents = 1000000;
        req.currency = "COP";
        req.customer_email = "pruebasensandbox@yopmail.com";
        req.reference = ReferenceGenerator.randomNequiReference();
        req.acceptance_token = acceptanceToken;

        req.payment_method = new BancolombiaQRTransactionRequest.PaymentMethod();
        req.payment_method.payment_description = "Pago a Tienda Wompi";
        req.payment_method.sandbox_status = "APPROVED";

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

    @Then("the QR response status should be {string}")
    public void theResponseStatusShouldBe(String status) {
        OnStage.theActorInTheSpotlight().should(
                seeThat(
                        actor -> SerenityRest.lastResponse().jsonPath()
                                .getString("data.status"),
                        equalTo(status)
                )
        );
    }

    @Then("after checking the transaction by ID, the status should be {string}")
    public void theTransactionStatusShouldBe(String expectedStatus) throws InterruptedException {

        String transactionId = SerenityRest.lastResponse().jsonPath().getString("data.id");

        Thread.sleep(1000);

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

        System.out.println("BancolombiaQR ID Transaction: " + sandboxId);
        System.out.println("BancolombiaQR Status: " + sandboxStatus);

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