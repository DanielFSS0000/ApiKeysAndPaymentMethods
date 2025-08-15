package co.com.wompi.api.stepdefinition;

import co.com.wompi.api.questions.GetQuestion;
import co.com.wompi.api.tasks.get.Call;
import co.com.wompi.api.utils.Constants;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import static io.restassured.http.ContentType.JSON;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class GetMerchantStepdefinition {
    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @When("the user queries the Wompi merchant with the public key")
    public void userConsultsMerchantWompiWithPublicKey() {
        String wompiBaseUrl = Constants.BASE_URL.replace(Constants.TYPE_ENVIRONMENT, "sandbox");
        String resource = "merchants/" + Constants.PUBLIC_KEY_WOMPI;

        OnStage.theActorCalled(Constants.ACTOR).attemptsTo(
                Call.service().apiget(wompiBaseUrl, resource, JSON.toString())
        );
    }

    @Then("it validates that the response is successfull")
    public void validSuccessfulResponse() {
        OnStage.theActorInTheSpotlight().should(seeThat(GetQuestion.successful(SC_OK)));
    }

    @Then("the field data.public_key must be {string}")
    public void publicKeyFieldMustBe(String publicKey) {
        OnStage.theActorInTheSpotlight().should(
                seeThat(
                        actor -> SerenityRest.lastResponse().jsonPath()
                                .getString("data.public_key"),
                        equalTo(publicKey)
                )
        );
    }
}