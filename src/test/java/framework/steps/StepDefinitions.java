package framework.steps;

import framework.client.BaseClient;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StepDefinitions {

    private final BaseClient client = new BaseClient();
    private Response response;

    @When("I send GET request to {string}")
    public void sendGetRequest(String path) {
        response = client.get(path);
    }

    @When("I send POST request to {string} with payload {string}")
    public void sendPostRequest(String path, String payload) {
        response = client.post(path, loadPayload(payload));
    }

    @When("I send PUT request to {string} with payload {string}")
    public void sendPutRequest(String path, String payload) {
        response = client.put(path, loadPayload(payload));
    }

    @When("I send DELETE request to {string}")
    public void sendDeleteRequest(String path) {
        response = client.delete(path);
    }

    @Then("status code should be {int}")
    public void statusCodeShouldBe(int expectedStatusCode) {
        assertEquals(expectedStatusCode, response.getStatusCode());
    }

    @Then("response should contain {string}")
    public void responseShouldContain(String expectedText) {
        assertTrue(response.getBody().asString().contains(expectedText));
    }

    private String loadPayload(String payload) {
        String resourcePath = payload.startsWith("payloads/") ? payload : "payloads/" + payload;
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (input != null) {
                return new String(input.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load payload: " + payload, e);
        }
        return payload;
    }

}
