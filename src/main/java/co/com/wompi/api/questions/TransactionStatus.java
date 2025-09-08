package co.com.wompi.api.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;


public class TransactionStatus implements Question<String> {
    private final String path;

    private TransactionStatus(String path){this.path = path;}

    @Override
    public String answeredBy(Actor actor) {

        return SerenityRest.lastResponse().jsonPath().getString((path));
    }
    public static TransactionStatus at(String jsonPath){
        return new TransactionStatus(jsonPath);
    }
}