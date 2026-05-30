package framework.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

public class SchemaValidator {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final JsonSchemaFactory schemaFactory =
            JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    public static void validate(String responseBody, String schemaFileName) {
        try {
            JsonSchema schema = loadSchema(schemaFileName);
            JsonNode responseNode = objectMapper.readTree(responseBody);
            Set<ValidationMessage> errors = schema.validate(responseNode);

            if (!errors.isEmpty()) {
                String errorMessages = errors.stream()
                        .map(ValidationMessage::getMessage)
                        .collect(Collectors.joining(System.lineSeparator()));
                throw new AssertionError("JSON schema validation failed:" + System.lineSeparator() + errorMessages);
            }
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to validate JSON schema: " + schemaFileName, e);
        }
    }

    private static JsonSchema loadSchema(String schemaFileName) throws IOException {
        String resourcePath = schemaFileName.startsWith("schemas/") ? schemaFileName : "schemas/" + schemaFileName;
        try (InputStream input = SchemaValidator.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("Schema file not found: " + resourcePath);
            }
            return schemaFactory.getSchema(input);
        }
    }

}
