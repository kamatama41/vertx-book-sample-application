package com.kamatama41.vertx;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.impl.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class MindMap {

    private JsonObject source;

    public MindMap(JsonObject source) {
        this.source = source;
    }

    public String getId() {
        return source.getString("_id");
    }

    public void setId(String id) {
        source.putString("_id", id);
    }

    public Node getNode() {
        return new Node(source);
    }

    public JsonObject toJson() {
        return source;
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

        public Node() {
            this(new JsonObject());
            setKey(newNodeKey());
        }

        public String getKey() {
            return source.getString("key");
        }

        public void setKey(String key) {
            this.source.putString("key", key);
        }

        public String getName() {
            return source.getString("name");
        }

        public void setName(String name) {
            source.putString("name", name);
        }

        public void addChild(Node child) {
            JsonArray children = source.getArray("children");
            if(children == null) {
                children = new JsonArray();
                source.putArray("children", children);
            }
            children.add(child.source);
        }

        public List<Node> removeChild(String key) {
            JsonArray children = source.getArray("children");
            if(children == null) {
                return Collections.emptyList();
            }
            final Iterator<Object> iterator = children.iterator();
            List<Node> removed = new ArrayList<>();
            while (iterator.hasNext()) {
                final JsonObject child = (JsonObject) iterator.next();
                if(child.getString("name").equals(key)) {
                    iterator.remove();
                    removed.add(new Node(child));
                }
            }
            return removed;
        }

        public List<Node> getChildren() {
            return createChildren(source);
        }

        public JsonObject toJson() {
            return source;
        }
    }

    private static List<Node> createChildren(JsonObject source) {
        List<Node> result = new ArrayList<>();
        for (Object child : source.getArray("children")) {
            if(!(child instanceof JsonObject)) {
                throw new IllegalStateException("Node.child isn't unexpected tyoe = " + child.getClass().getCanonicalName());
            }
            result.add(new Node((JsonObject)child));
        }
        return result;
    }

    private static String newNodeKey() {
        return UUID.randomUUID().toString();
    }

}
