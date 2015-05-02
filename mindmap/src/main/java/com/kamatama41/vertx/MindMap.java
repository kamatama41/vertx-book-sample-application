package com.kamatama41.vertx;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MindMap {
    private Integer id;
    private String name;

    @JsonCreator
    public MindMap(
            @JsonProperty(value = "_id", required = false) Integer id,
            @JsonProperty(value = "name", required = false) String name) {
        this.id = id;
        this.name = name;
    }

    @JsonProperty("_id")
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
