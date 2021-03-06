package com.ultimatesoftware.workflow.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ultimatesoftware.workflow.messaging.GenericMessage;

import java.util.UUID;

import static com.ultimatesoftware.workflow.messaging.Constants.ZERO_UUID;
import static com.ultimatesoftware.workflow.messaging.kafka.TestConstants.*;

public class GenericMessageBuilder {

    private final String id = UUID.randomUUID().toString();
    private final String schemaVersion = "1.0";
    private String tenantId = GENERIC_TENANT_ID;
    private final String messageType = GENERIC_MESSAGE_TYPE;
    private ObjectNode body = new ObjectMapper().createObjectNode()
        .put(GENERIC_BUSINESS_PROCESS_KEY_FIELD, GENERIC_BUSINESS_PROCESS_KEY_VALUE)
        .put("name", "name");

    public GenericMessageBuilder withSystemTenant() {
        this.tenantId = ZERO_UUID;
        return this;
    }

    public GenericMessage build() {
        return new GenericMessage(id, schemaVersion, tenantId, messageType, body);
    }
}
