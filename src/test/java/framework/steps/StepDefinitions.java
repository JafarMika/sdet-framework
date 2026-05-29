package framework.steps;

import framework.client.BaseClient;
import framework.database.DatabaseHelper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(StepDefinitions.class);

    private final BaseClient client = new BaseClient();
    private DatabaseHelper databaseHelper;
    private Response response;
    private ResultSet resultSet;

    @When("I send GET request to {string}")
    public void sendGetRequest(String path) {
        logStepStart("I send GET request to \"" + path + "\"");
        response = client.get(path);
    }

    @When("I send POST request to {string} with payload {string}")
    public void sendPostRequest(String path, String payload) {
        logStepStart("I send POST request to \"" + path + "\" with payload \"" + payload + "\"");
        response = client.post(path, loadPayload(payload));
    }

    @When("I send PUT request to {string} with payload {string}")
    public void sendPutRequest(String path, String payload) {
        logStepStart("I send PUT request to \"" + path + "\" with payload \"" + payload + "\"");
        response = client.put(path, loadPayload(payload));
    }

    @When("I send DELETE request to {string}")
    public void sendDeleteRequest(String path) {
        logStepStart("I send DELETE request to \"" + path + "\"");
        response = client.delete(path);
    }

    @Then("status code should be {int}")
    public void statusCodeShouldBe(int expectedStatusCode) {
        logStepStart("status code should be " + expectedStatusCode);
        assertEquals(expectedStatusCode, response.getStatusCode());
    }

    @Then("response should contain {string}")
    public void responseShouldContain(String expectedText) {
        logStepStart("response should contain \"" + expectedText + "\"");
        assertTrue(response.getBody().asString().contains(expectedText));
    }

    @When("I query the database with {string}")
    public void queryDatabase(String sql) throws SQLException {
        logStepStart("I query the database with \"" + sql + "\"");
        log.info("Executing database query: {}", sql);
        resultSet = getDatabaseHelper().executeQuery(sql);
    }

    @Then("the result should contain {string}")
    public void resultShouldContain(String expectedValue) throws SQLException {
        logStepStart("the result should contain \"" + expectedValue + "\"");
        assertNotNull("No query was executed", resultSet);
        assertTrue("Result set does not contain: " + expectedValue, containsValue(resultSet, expectedValue));
    }

    private void logStepStart(String step) {
        log.info("Step start: {}", step);
    }

    private DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper();
        }
        return databaseHelper;
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
