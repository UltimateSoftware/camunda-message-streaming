package com.ultimatesoftware.workflow.messaging.correlation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class JsonNodeEvaluator {

    private final ObjectMapper objectMapper = new ObjectMapper()
        .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

    public JsonNodeEvaluator() {
    }

    public Object evaluateNode(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }

        if (node.isTextual()) {
            return node.textValue();
        } else if (node.isArray()) {
            return evaluateArrayNode((ArrayNode) node);
        } else if (node.isPojo() || node.isObject()) {
            return evaluateObjectNode(node);
        } else if (node.isInt()) {
            return node.intValue();
        } else if (node.isBoolean()) {
            return node.booleanValue();
        } else if (node.isDouble()) {
            return node.doubleValue();
        } else {
            return node.toString();
        }
    }

    private Object evaluateArrayNode(ArrayNode arrayNode) {
        List<Object> list = new ArrayList<>(arrayNode.size());

        arrayNode.forEach(element -> list.add(evaluateNode(element)));
        return list;
    }

    private Object evaluateObjectNode(JsonNode node) {
        try {
            String jsonString = objectMapper.writeValueAsString(node);
            Map<String, Object> objectMap = objectMapper.readValue(jsonString, Map.class);
            return objectMap;
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(String.format("Unable to parse ObjectNode value %s into Map",
                node.toString()), ex);
        }
    }
}
