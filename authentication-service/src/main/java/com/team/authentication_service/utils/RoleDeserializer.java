package com.team.authentication_service.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.team.authentication_service.models.Role;
import java.io.IOException;
import java.util.List;

public class RoleDeserializer extends JsonDeserializer<List<Role>> {
    @Override
    public List<Role> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        // Correct the TypeReference to List<Role>
        return jsonParser.readValueAs(new TypeReference<List<Role>>() {});
    }
}
