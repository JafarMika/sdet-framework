package framework.client;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Supplier;

public class BaseClient {

    private static final Logger log = LoggerFactory.getLogger(BaseClient.class);

    private final String baseUrl;

    public BaseClient() {
        this(loadBaseUrl());
    }

    public BaseClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response get(String path) {
        return execute("GET", path, () -> request().get(path));
    }

    public Response post(String path) {
        return execute("POST", path, () -> request().post(path));
    }

    public Response post(String path, Object body) {
        return execute("POST", path, () -> request().body(body).post(path));
    }

    public Response put(String path) {
        return execute("PUT", path, () -> request().put(path));
    }

    public Response put(String path, Object body) {
        return execute("PUT", path, () -> request().body(body).put(path));
    }

    public Response delete(String path) {
        return execute("DELETE", path, () -> request().delete(path));
    }

    private Response execute(String method, String path, Supplier<Response> request) {
        log.info("Request: {} {}", method, baseUrl + path);
        long startTime = System.currentTimeMillis();
        Response response = request.get();
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("Response: status code {} ({} ms)", response.getStatusCode(), elapsedTime);
        return response;
    }

    private RequestSpecification request() {
        return RestAssured.given().baseUri(baseUrl);
    }

    private static String loadBaseUrl() {
        Properties properties = new Properties();
        try (InputStream input = BaseClient.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("config.properties not found on classpath");
            }
            properties.load(input);
            String url = properties.getProperty("base.url");
            if (url == null || url.isBlank()) {
                throw new IllegalStateException("base.url is not set in config.properties");
            }
            return url.trim();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
    }

}
