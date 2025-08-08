package co.com.wompi.api.tasks.post;

import co.com.wompi.api.interactions.PostRequest;
import co.com.wompi.api.models.NequiTransactionRequest;
import com.google.gson.Gson;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;

public class CreateNequiTransaction implements Task {
    private final NequiTransactionRequest request;
    private final String baseUrl;
    private final String bearerToken;

    public CreateNequiTransaction(NequiTransactionRequest request,
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

    public static CreateNequiTransaction with(NequiTransactionRequest request,
                                              String baseUrl, String bearerToken) {
        return Tasks.instrumented(CreateNequiTransaction.class, request, baseUrl, bearerToken);
    }
}
