package co.com.wompi.api.tasks.post;

import co.com.wompi.api.interactions.PostRequest;
import co.com.wompi.api.models.PcolTransactionRequest;

import com.google.gson.Gson;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;

public class PostPcolPaymentMethod implements Task {
    private final PcolTransactionRequest request;
    private final String baseUrl;
    private final String bearerToken;

    public PostPcolPaymentMethod(PcolTransactionRequest request,
                                 String baseUrl, String bearerToken) {
        this.request = request;
        this.baseUrl = baseUrl;
        this.bearerToken = bearerToken;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Gson gson = new Gson();
        String bodyJson = gson.toJson(request);

        actor.attemptsTo(
                PostRequest.to(baseUrl, "transactions", bodyJson, bearerToken)
        );
    }

    public static PostPcolPaymentMethod with(PcolTransactionRequest request,
                                             String baseUrl, String bearerToken) {
        return Tasks.instrumented(PostPcolPaymentMethod.class, request, baseUrl, bearerToken);
    }
}

