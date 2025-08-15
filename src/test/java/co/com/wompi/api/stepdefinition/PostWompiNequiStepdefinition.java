
package co.com.wompi.api.stepdefinition;

import co.com.wompi.api.models.NequiTransactionRequest;
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

        String acceptanceToken = SerenityRest
                .given()
                .get(wompiBaseUrl + "merchants/" + Constants.PUBLIC_KEY_WOMPI)
                .jsonPath()
                .getString("data.presigned_acceptance.acceptance_token");

        NequiTransactionRequest req = new NequiTransactionRequest();
        req.amount_in_cents = 1000000;
        req.currency = "COP";
        req.customer_email = "pruebasensandbox@yopmail.com";
        req.reference = "pedido-nequi-123456";
        req.acceptance_token = acceptanceToken;
        req.payment_method = new NequiTransactionRequest.PaymentMethod();
        req.payment_method.phone_number = "3991111111";

        req.signature = SignatureUtils.calcularFirmaIntegridad(
                req.reference = ReferenceGenerator.randomNequiReference(),
                req.amount_in_cents,
                req.currency,
                Constants.INTEGRITY_KEY_WOMPI
        );

        System.out.println("[Debug] Cadena para firmar: " + req.reference + req.amount_in_cents + req.currency +
                Constants.INTEGRITY_KEY_WOMPI);

        System.out.println("[Debug] Firma generada: " + req.signature);

        OnStage.theActorCalled(Constants.ACTOR).attemptsTo(
                CreateTransaction.with(req, wompiBaseUrl, "transactions", Constants.PRIVATE_KEY_WOMPI)
        );

    }

    @Then("the response status should be {string}")
    public void responseStatusShouldPENDING(String expectedStatus) {
        OnStage.theActorInTheSpotlight().should(
                seeThat(
                        actor -> SerenityRest.lastResponse().jsonPath()
                                .getString("data.status"),
                        equalTo(expectedStatus)
                )
        );
    }
    @Then("after a few minutes the user validates the {string} status")
    public void theTransactionIsApproved(String expectedStatus) throws InterruptedException {
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

        System.out.println("=== ESTADO FINAL DE LA TRANSACCIÓN NEQUI ===");
        String sandboxId = SerenityRest.lastResponse().jsonPath().getString("data.id");
        String sandboxStatus = SerenityRest.lastResponse().jsonPath().getString("data.status");
        System.out.println("Nequi Id Transaction: " + sandboxId);
        System.out.println("Nequi Status: " + sandboxStatus);

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