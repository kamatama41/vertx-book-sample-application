package com.kamatama41.vertx;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.impl.Json;

import java.io.IOException;

public class MindMap {
    private static ObjectMapper mapper = new ObjectMapper();

    private String id;
    private String name;

    @JsonCreator
    public MindMap(
            @JsonProperty(value = "_id", required = false) String id,
            @JsonProperty(value = "name", required = false) String name) {
        this.id = id;
        this.name = name;
    }

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return Json.encode(this);
    }


    public JsonObject toJson() {
        try {
            return new JsonObject(mapper.writeValueAsString(this));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static MindMap fromJson(JsonObject json) {
        try {
            return mapper.readValue(json.toString(), MindMap.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
