package co.com.wompi.api.stepdefinition;

import co.com.wompi.api.models.BancolombiaQRTransactionRequest;
import co.com.wompi.api.tasks.post.CreateBancolombiaQRTransaction;
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

    @When("the user creates a Bancolombia QR transaction in Wompi with PENDING status")
    public void userCreatesBancolombiaQRTransaction() {
        // Get URL from environment
        String wompiBaseUrlKey = Constants.BASE_URL.replace(Constants.TYPE_ENVIRONMENT, "sandbox");
        String wompiBaseUrl = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty(wompiBaseUrlKey);

        // Get acceptance token
        String acceptanceToken = SerenityRest
                .given()
                .get(wompiBaseUrl + "merchants/" + Constants.PUBLIC_KEY_WOMPI)
                .jsonPath()
                .getString("data.presigned_acceptance.acceptance_token");

        // Build request
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
                CreateBancolombiaQRTransaction.with(req, wompiBaseUrl, Constants.PRIVATE_KEY_WOMPI)
        );
    }

    @Then("the response status should be {string}")
    public void theResponseStatusShouldBe(String status) {
        OnStage.theActorInTheSpotlight().should(
                seeThat(
                        actor -> SerenityRest.lastResponse().jsonPath().getString("data.status"),
                        equalTo(status)
                )
        );
    }

    @Then("the payment method should be {string}")
    public void thePaymentMethodShouldBe(String method) {
        OnStage.theActorInTheSpotlight().should(
                seeThat(
                        actor -> SerenityRest.lastResponse().jsonPath().getString("data.payment_method.type"),
                        equalTo(method)
                )
        );
    }
}
