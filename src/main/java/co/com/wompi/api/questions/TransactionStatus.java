package co.com.wompi.api.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class TransactionStatus implements Question<String> {

    public static TransactionStatus status() {
        return new TransactionStatus();
    }

    @Override
    public String answeredBy(Actor actor) {

        return SerenityRest.lastResponse().jsonPath().getString("data.status");
    }
}