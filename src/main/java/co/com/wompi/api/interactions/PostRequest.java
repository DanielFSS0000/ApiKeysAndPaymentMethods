package co.com.wompi.api.interactions;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Post;

@Slf4j
public class PostRequest implements Interaction {
    private final String baseUrl;
    private final String endpoint;
    private final String body;
    private final String bearerToken;

    public PostRequest(String baseUrl, String endpoint, String body, String bearerToken) {
        this.baseUrl = baseUrl;
        this.endpoint = endpoint;
        this.body = body;
        this.bearerToken = bearerToken;
    }
    @Override
    @Subject("{0} consume el servicio POST {#endpoint}")
    public <T extends Actor> void performAs(T actor) {
        log.info("POST to {}{} with body: {}", baseUrl, endpoint, body);
        actor.whoCan(CallAnApi.at(baseUrl));
        actor.attemptsTo(
                Post.to(endpoint)
                        .with(req -> req
                                .header("Authorization", "Bearer " + bearerToken)
                                .header("Content-Type", "application/json")
                                .body(body)
                                .relaxedHTTPSValidation()
                        )
        );
        log.info("[Debug] BASE_URL usada: {}", baseUrl);
        log.info("[Debug] ENDPOINT usado: {} ", endpoint);
        log.info("[Debug] JSON BODY: {} ", body);
        log.info("[Debug] JSON BODY: {} ", bearerToken);
        SerenityRest.lastResponse().prettyPrint();
    }
    public static PostRequest to(String baseUrl, String endpoint, String body, String bearerToken) {
        return Tasks.instrumented(PostRequest.class, baseUrl, endpoint, body, bearerToken);
    }
}