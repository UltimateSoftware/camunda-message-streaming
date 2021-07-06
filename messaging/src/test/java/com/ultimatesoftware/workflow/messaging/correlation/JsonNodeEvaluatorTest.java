package com.ultimatesoftware.workflow.messaging.correlation;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class JsonNodeEvaluatorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JsonNodeEvaluator jsonNodeEvaluator = new JsonNodeEvaluator();

    @Test
    public void whenJsonNodeIsNull_shouldReturnNull() {
        Object object = jsonNodeEvaluator.evaluateNode(null);

        assertThat(object).isNull();

        JsonNode node = NullNode.getInstance();
        object = jsonNodeEvaluator.evaluateNode(node);

        assertThat(object).isNull();
    }

    @Test
    public void whenJsonNodeIsString_shouldReturnString() {
        JsonNode node = new TextNode("string");

        Object object = jsonNodeEvaluator.evaluateNode(node);

        assertThat(object).isEqualTo("string");
    }

    @Test
    public void whenJsonNodeIsInt_shouldReturnInt() {
        JsonNode node = new IntNode(100);

        Object object = jsonNodeEvaluator.evaluateNode(node);

        assertThat(object).isEqualTo(100);
    }

    @Test
    public void whenJsonNodeIsBoolean_shouldReturnBoolean() {
        JsonNode node = BooleanNode.valueOf(true);

        Object object = jsonNodeEvaluator.evaluateNode(node);

        assertThat(object).isEqualTo(true);
    }

    @Test
    public void whenJsonNodeIsDouble_shouldReturnString() {
        JsonNode node = new DoubleNode(1.23);

        Object object = jsonNodeEvaluator.evaluateNode(node);

        assertThat(object).isEqualTo(1.23);
    }

    @Test
    public void whenJsonNodeIsObject_shouldReturnMap() throws JsonProcessingException {
        JsonNode node = objectMapper
            .createObjectNode()
            .put("string", "string")
            .put("int", 1);

        Object object = jsonNodeEvaluator.evaluateNode(node);
        String jsonString = objectMapper.writeValueAsString(object);
        Map resultMap = objectMapper.readValue(jsonString, Map.class);

        Map expectedMap = new HashMap(){{
            put("string", "string");
            put("int", 1);
        }};

        assertThat(resultMap).isEqualTo(expectedMap);
    }

    @Test
    public void whenJsonNodeIsArray_shouldReturnList() {
        JsonNode node = objectMapper
            .createArrayNode()
            .add("string");

        Object object = jsonNodeEvaluator.evaluateNode(node);

        assertThat(object).isEqualTo(Arrays.asList("string"));
    }
}
