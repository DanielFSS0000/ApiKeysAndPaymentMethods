package co.com.wompi.api.interactions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostRequest implements Interaction {
    private static final Logger log = LoggerFactory.getLogger(PostRequest.class);

    private final String baseUrl; // [NEW] Para manejar la base de la URL
    private final String endpoint; // Ej: "transactions"
    private final String body;
    private final String bearerToken;

    public PostRequest(String baseUrl, String endpoint, String body, String bearerToken) {
        this.baseUrl = baseUrl;
        this.endpoint = endpoint;
        this.body = body;
        this.bearerToken = bearerToken;
        System.out.println("[Debug] BASE_URL usada: " + baseUrl);
        System.out.println("[Debug] ENDPOINT usado: " + endpoint);
        System.out.println("[Debug] JSON BODY: " + body);
    }

    @Override
    @Subject("{0} consume el servicio POST {#endpoint}")
    public <T extends Actor> void performAs(T actor) {
        log.info("POST to {}/{} with body: {}", baseUrl, endpoint, body);
        actor.whoCan(CallAnApi.at(baseUrl));
        actor.attemptsTo(
                Post.to(endpoint)
                        .with(req -> req
                                .header("Authorization", "Bearer " + bearerToken)
                                .header("Content-Type", "application/json")
                                .body(body)
                        )
        );
        SerenityRest.lastResponse().prettyPrint();
    }

    public static PostRequest to(String baseUrl, String endpoint, String body, String bearerToken) {
        return Tasks.instrumented(PostRequest.class, baseUrl, endpoint, body, bearerToken);
    }
}
