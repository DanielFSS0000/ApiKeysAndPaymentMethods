package co.com.wompi.api.tasks.post;

import co.com.wompi.api.interactions.PostRequest;
import co.com.wompi.api.models.PseTransactionRequest;
import com.google.gson.Gson;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;

public class CreatePseTransaction implements Task {

    private final PseTransactionRequest request;
    private final String baseUrl;
    private final String privateKey;

    // Constructor
    public CreatePseTransaction(PseTransactionRequest request,
                                String baseUrl, String privateKey) {
        this.request = request;
        this.baseUrl = baseUrl;
        this.privateKey = privateKey;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String bodyJson = new Gson().toJson(request);
        actor.attemptsTo(
                PostRequest.to(baseUrl, "transactions", bodyJson, privateKey)
        );
    }

    public static CreatePseTransaction with(PseTransactionRequest request,
                                            String baseUrl, String privateKey) {
        return Tasks.instrumented(CreatePseTransaction.class, request, baseUrl, privateKey);
    }
}
