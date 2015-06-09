package com.kamatama41.vertx;

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.List;

public class MindMapEditorVerticle extends Verticle {

    @Override
    public void start() {
        vertx.eventBus().registerHandler("mindMaps.editor.addNode", (Message<JsonObject> request) -> {
            final JsonObject reqBody = request.body();
            final String mindMapId = reqBody.getString("mindMapId");
            final String parentKey = reqBody.getString("parentKey");
            final String name = reqBody.getString("name");

            vertx.eventBus().send("mindMaps.find"
                    , new JsonObject().putString("_id", mindMapId)
                    , (Message<JsonObject> response) -> {
                        final JsonObject mindMapJson = response.body().getObject("mindMap");
                        if(mindMapJson == null) {
                            return;
                        }

                        MindMap mindMap = new MindMap(mindMapJson);
                        MindMap.Node parent = findNodeByKey(mindMap.getNode(), parentKey);
                        MindMap.Node newNode = new MindMap.Node();

                        if(name != null) {
                            newNode.setName(name);
                        } else {
                            newNode.setName("Click to edit");
                        }
                        parent.addChild(newNode);

                        vertx.eventBus().send("mindMaps.save", mindMap.toJson(), (Message<JsonObject> ignore) ->{
                            publishMindMapEvent(mindMap
                                    , new JsonObject()
                                    .putString("event", "nodeAdded")
                                            .putString("parentKey", parentKey)
                                            .putObject("node", newNode.toJson())
                            );
                        });
                    }
            );
        });

        vertx.eventBus().registerHandler("mindMaps.editor.renameNode", (Message<JsonObject> request) -> {
            final JsonObject reqBody = request.body();
            final String mindMapId = reqBody.getString("mindMapId");
            final String key = reqBody.getString("key");
            final String newName = reqBody.getString("newName");
            vertx.eventBus().send("mindMaps.find"
                    , new JsonObject().putString("_id", mindMapId)
                    , (Message<JsonObject> result) -> {
                        final JsonObject mindMapJson = result.body().getObject("mindMap");
                        if(mindMapJson == null) {
                            return;
                        }

                        MindMap mindMap = new MindMap(mindMapJson);
                        MindMap.Node node = findNodeByKey(mindMap.getNode(), key);
                        if(node == null) {
                            return;
                        }
                        node.setName(newName);
                        vertx.eventBus().send("mindMaps.save", mindMap.toJson(), (Message<JsonObject> ignore) -> {
                            JsonObject response = new JsonObject()
                                    .putString("event", "nodeRenamed")
                                    .putString("newName", newName);
                            // "key == null" is root node.
                            if(key != null) { response.putString("key", key); }
                            publishMindMapEvent(mindMap, response);
                        });
                    }
            );
        });

        vertx.eventBus().registerHandler("mindMaps.editor.deleteNode", (Message<JsonObject> request) -> {
            final JsonObject reqBody = request.body();
            final String mindMapId = reqBody.getString("mindMapId");
            final String parentKey = reqBody.getString("parentKey");
            final String key = reqBody.getString("key");
            vertx.eventBus().send("mindMaps.find"
                    , new JsonObject().putString("_id", mindMapId)
                    , (Message<JsonObject> response) -> {
                        final JsonObject mindMapJson = response.body().getObject("mindMap");
                        if(mindMapJson == null) {
                            return;
                        }

                        MindMap mindMap = new MindMap(mindMapJson);
                        MindMap.Node parent = findNodeByKey(mindMap.getNode(), parentKey);
                        final List<MindMap.Node> removed = parent.removeChild(key);

                        vertx.eventBus().send("mindMaps.save", mindMap.toJson(), (Message<JsonObject> ignore) -> {
                            removed.forEach(node -> {
                                publishMindMapEvent(mindMap
                                        , new JsonObject()
                                        .putString("event", "nodeDeleted")
                                        .putString("parentKey", parentKey)
                                        .putString("key", key)
                                );
                            });
                        });
                    }
            );
        });
    }

    private MindMap.Node findNodeByKey(MindMap.Node root, String key) {
        if(root.getKey() == null && key == null) {
            return root;
        } else if(root.getChildren() != null) {
            for (MindMap.Node child : root.getChildren()) {
                MindMap.Node match = findNodeByKey(child, key);
                if(match != null) {
                    return match;
                }
            }
        }
        return null;
    }

    private void publishMindMapEvent(MindMap mindMap, Object event) {
        vertx.eventBus().publish("mindMaps.events." + mindMap.getId(), event);
    }

}
