package co.com.wompi.api.tasks.post;

import co.com.wompi.api.interactions.PostRequest;

import com.google.gson.Gson;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;

public class CreateTransaction<T> implements Task {
    private final T request;
    private final String baseUrl;
    private final String endpoint;
    private final String privateKey;

    public CreateTransaction(T request, String baseUrl, String endpoint, String privateKey) {
        this.request = request;
        this.baseUrl = baseUrl;
        this.endpoint = endpoint;
        this.privateKey = privateKey;
    }

    @Override
    public <A extends Actor> void performAs(A actor) {
        String jsonBody = new Gson().toJson(request);
        actor.attemptsTo(
                PostRequest.to(baseUrl, endpoint, jsonBody, privateKey)
        );
    }

    public static <T> CreateTransaction<T> with(T request, String baseUrl, String endpoint, String privateKey) {
        return Tasks.instrumented(CreateTransaction.class, request, baseUrl, endpoint, privateKey);
    }
}
