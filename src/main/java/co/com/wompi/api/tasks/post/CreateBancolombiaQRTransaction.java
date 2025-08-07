package co.com.wompi.api.tasks.post;

import co.com.wompi.api.interactions.PostRequest;
import co.com.wompi.api.models.BancolombiaQRTransactionRequest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import com.google.gson.Gson;

public class CreateBancolombiaQRTransaction implements Task {
    private final BancolombiaQRTransactionRequest request;
    private final String baseUrl;
    private final String privateKey;

    public CreateBancolombiaQRTransaction(BancolombiaQRTransactionRequest request, String baseUrl, String privateKey) {
        this.request = request;
        this.baseUrl = baseUrl;
        this.privateKey = privateKey;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String jsonBody = new Gson().toJson(request);
        actor.attemptsTo(
                PostRequest.to(baseUrl, "transactions", jsonBody, privateKey)
        );
    }

    public static CreateBancolombiaQRTransaction with(BancolombiaQRTransactionRequest request, String baseUrl, String privateKey) {
        return Tasks.instrumented(CreateBancolombiaQRTransaction.class, request, baseUrl, privateKey);
    }
}
