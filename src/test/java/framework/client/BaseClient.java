package framework.client;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BaseClient {

    private final String baseUrl;

    public BaseClient() {
        this(loadBaseUrl());
    }

    public BaseClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response get(String path) {
        return request().get(path);
    }

    public Response post(String path) {
        return request().post(path);
    }

    public Response post(String path, Object body) {
        return request().body(body).post(path);
    }

    public Response put(String path) {
        return request().put(path);
    }

    public Response put(String path, Object body) {
        return request().body(body).put(path);
    }

    public Response delete(String path) {
        return request().delete(path);
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
