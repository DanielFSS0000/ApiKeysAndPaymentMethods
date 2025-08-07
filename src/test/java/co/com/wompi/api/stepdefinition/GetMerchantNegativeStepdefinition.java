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
import static org.hamcrest.Matchers.equalTo;

public class GetMerchantNegativeStepdefinition {
    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @When("the user queries the Wompi merchant with an invalid public key")
    public void userQueriesMerchantWithInvalidPublicKey() {
        String wompiBaseUrl = Constants.BASE_URL.replace(Constants.TYPE_ENVIRONMENT, "sandbox");
        String invalidResource = "merchants/INVALID_PUBLIC_KEY_123";
        OnStage.theActorCalled(Constants.ACTOR).attemptsTo(
                Call.service().apiget(wompiBaseUrl, invalidResource, JSON.toString())
        );
    }

    @Then("it validates that the response code is {int}")
    public void itValidatesThatTheResponseCodeIs(int statusCode) {
        OnStage.theActorInTheSpotlight().should(seeThat(GetQuestion.successful(statusCode)));
    }

    @Then("the error message should be {string}")
    public void theErrorMessageShouldBe(String errorMessage) {
        OnStage.theActorInTheSpotlight().should(
                seeThat(
                        actor -> SerenityRest.lastResponse().jsonPath().getString("error.messages.public_key[0]"),
                        equalTo(errorMessage)
                )
        );
    }
}
