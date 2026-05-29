package framework.steps;

import framework.client.BaseClient;
import framework.database.DatabaseHelper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StepDefinitions {

    private final BaseClient client = new BaseClient();
    private final DatabaseHelper databaseHelper = new DatabaseHelper();
    private Response response;
    private ResultSet resultSet;

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

    @When("I query the database with {string}")
    public void queryDatabase(String sql) throws SQLException {
        resultSet = databaseHelper.executeQuery(sql);
    }

    @Then("the result should contain {string}")
    public void resultShouldContain(String expectedValue) throws SQLException {
        assertNotNull("No query was executed", resultSet);
        assertTrue("Result set does not contain: " + expectedValue, containsValue(resultSet, expectedValue));
    }

    private boolean containsValue(ResultSet rs, String expectedValue) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            for (int column = 1; column <= columnCount; column++) {
                Object value = rs.getObject(column);
                if (value != null && value.toString().contains(expectedValue)) {
                    return true;
                }
            }
        }
        return false;
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
