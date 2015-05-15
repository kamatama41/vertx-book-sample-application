package com.kamatama41.vertx;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.impl.Json;

import java.util.ArrayList;
import java.util.List;

public class MindMap {

    private JsonObject source;

    public MindMap(JsonObject source) {
        this.source = source;
    }

    public MindMap() {
        this(new JsonObject().putArray("children", new JsonArray()));
    }

    public String getId() {
        return source.getString("_id");
    }

    public void setId(String id) {
        source.putString("_id", id);
    }

    public String getName() {
        return source.getString("name");
    }

    public void setName(String name) {
        source.putString("name", name);
    }

    @Override
    public String toString() {
        return Json.encode(this);
    }

    public static class Node {
        private JsonObject source;

        private Node(JsonObject source) {
            this.source = source;
        }

        private Node() {
            this(new JsonObject().putArray("children", new JsonArray()));
        }

        public String getKey() {
            return source.getString("key");
        }

        public List<Node> getChildren() {
            List<Node> result = new ArrayList<>();
            for (Object child : source.getArray("children")) {
                if(!(child instanceof JsonObject)) {
                    throw new IllegalStateException("Node.child isn't unexpected tyoe = " + child.getClass().getCanonicalName());
                }
                result.add(new Node((JsonObject)child));
            }
            return result;
        }
    }

}
