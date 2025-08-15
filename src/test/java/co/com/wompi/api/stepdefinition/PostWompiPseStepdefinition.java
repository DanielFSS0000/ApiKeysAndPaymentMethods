package co.com.wompi.api.stepdefinition;

import co.com.wompi.api.models.PseTransactionRequest;
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
import static org.hamcrest.MatcherAssert.assertThat;
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

        String acceptanceToken = SerenityRest
                .given()
                .get(wompiBaseUrl + "merchants/" + Constants.PUBLIC_KEY_WOMPI)
                .jsonPath()
                .getString("data.presigned_acceptance.acceptance_token");

        PseTransactionRequest req = new PseTransactionRequest();
        req.amount_in_cents = 1000000;
        req.currency = "COP";
        req.customer_email = "pruebasensandbox@yopmail.com";
        req.reference = ReferenceGenerator.randomNequiReference();
        req.acceptance_token = acceptanceToken;

        req.payment_method = new PseTransactionRequest.PaymentMethod();
        req.payment_method.type = "PSE";
        req.payment_method.user_type = 0;
        req.payment_method.user_legal_id_type = "CC";
        req.payment_method.user_legal_id = "1999888777";
        req.payment_method.financial_institution_code = "1";
        req.payment_method.payment_description = "Pago Tienda Wompi";


        req.signature = SignatureUtils.calcularFirmaIntegridad(
                req.reference, req.amount_in_cents, req.currency, Constants.INTEGRITY_KEY_WOMPI
        );

        OnStage.theActorCalled(Constants.ACTOR).attemptsTo(
                CreateTransaction.with(req, wompiBaseUrl, "transactions", Constants.PRIVATE_KEY_WOMPI)
        );
    }

    @Then("the PSE response status should be {string}")
    public void theResponseStatusShouldBePending(String expectedStatus) {
        String actualReason = SerenityRest.lastResponse().jsonPath().getString("data.status");
        assertThat(actualReason, equalTo(expectedStatus));
    }

    @Then("the user waits a few minutes to validate the {string} status")
    public void theTransactionPseIsDeclined(String expectedStatus) throws InterruptedException {
        String transactionId = SerenityRest.lastResponse().jsonPath().getString("data.id");

        Thread.sleep(4000);

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