
package co.com.wompi.api.stepdefinition;

import co.com.wompi.api.models.NequiTransactionRequest;
import co.com.wompi.api.tasks.post.CreateNequiTransaction;
import co.com.wompi.api.questions.TransactionStatus;
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

        String merchantUrl = wompiBaseUrl + "merchants/" + Constants.PUBLIC_KEY_WOMPI;

        String acceptanceToken = SerenityRest
                .given()
                .get(merchantUrl)
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
                CreateNequiTransaction.with(req, wompiBaseUrl, Constants.PRIVATE_KEY_WOMPI)
        );

    }

    @Then("the response status should be PENDING")
    public void responseStatusShouldPENDING() {
        OnStage.theActorInTheSpotlight().should(
                seeThat(TransactionStatus.status(), equalTo("PENDING"))
        );
    }

    @Then("the payment method should be NEQUI")
    public void validNequiPaymentMethod() {
        OnStage.theActorInTheSpotlight().should(
                seeThat(
                        actor -> SerenityRest.lastResponse()
                                .jsonPath()
                                .getString("data.payment_method.type"),
                        equalTo("NEQUI")
                )
        );
    }
}
