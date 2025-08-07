package co.com.wompi.api.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "co.com.wompi.api.stepdefinition",
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class RunnerApiWompi {}
