package com.ultimatesoftware.workflow.messaging.builders;

import static com.ultimatesoftware.workflow.messaging.TestConstants.GENERIC_MESSAGE_TYPE;
import static com.ultimatesoftware.workflow.messaging.TestConstants.GENERIC_TENANT_ID;

import com.fasterxml.jackson.databind.JsonNode;
import com.ultimatesoftware.workflow.messaging.GenericMessage;
import java.util.UUID;

public class GenericMessageBuilder {

    private final String id = UUID.randomUUID().toString();
    private final String schemaVersion = "1.0";
    private final String tenantId = GENERIC_TENANT_ID;
    private final String messageType = GENERIC_MESSAGE_TYPE;
    private JsonNode body;

    public GenericMessage build() {
        return new GenericMessage(id, schemaVersion, tenantId, messageType, body);
    }
}