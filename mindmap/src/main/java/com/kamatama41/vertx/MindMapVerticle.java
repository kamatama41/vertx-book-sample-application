package com.kamatama41.vertx;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class MindMapVerticle extends Verticle {

    @Override
    public void start() {
        // Find all MindMap
        vertx.eventBus().registerHandler("mindMaps.list", event -> {
            executeMongoAction("find"
                    , new JsonObject().putObject("matcher", new JsonObject())
                    , (Message<JsonObject> message) -> {
                        final JsonObject response = message.body();
                        if(response.getString("status").equals("ok")) {
                            event.reply(new JsonObject().putArray("mindMaps", response.getArray("results")));
                        } else {
                            container.logger().warn(response.getString("message"));
                        }
                    }
            );
        });

        // Find specific MindMap
        vertx.eventBus().registerHandler("mindMaps.find", (Message<JsonObject> event) -> {
            executeMongoAction("find"
                    , new JsonObject().putObject("matcher", new JsonObject().putString("_id", event.body().getString("_id")))
                    , (Message<JsonObject> message) -> {
                        final JsonObject response = message.body();
                        if(response.getString("status").equals("ok")) {
                            event.reply(new JsonObject().putArray("mindMaps", response.getArray("results")));
                        } else {
                            container.logger().warn(response.getString("message"));
                        }
                    }
            );
        });

        // Save MindMap
        vertx.eventBus().registerHandler("mindMaps.save", (Message<JsonObject> event) -> {
            final JsonObject mindMap = event.body();
            executeMongoAction("save"
                    , new JsonObject().putObject("document", mindMap)
                    , (Message<JsonObject> message) -> {
                        final JsonObject response = message.body();
                        if (response.getString("status").equals("ok")) {
                            mindMap.putString("_id", response.getString("_id"));
                            event.reply(mindMap);
                        } else {
                            container.logger().warn(response.getString("message"));
                        }
                    }
            );
        });

        // Delete specific MindMap
        vertx.eventBus().registerHandler("mindMaps.delete", (Message<JsonObject> event) -> {
            executeMongoAction("delete"
                    , new JsonObject().putObject("matcher", new JsonObject().putString("_id", event.body().getString("_id")))
                    , (Message<JsonObject> message) -> {
                        final JsonObject response = message.body();
                        if (response.getString("status").equals("ok")) {
                            event.reply(new JsonObject());
                        } else {
                            container.logger().warn(response.getString("message"));
                        }
                    }
            );
        });
    }

    private <T> void executeMongoAction(String action, JsonObject config, Handler<Message<T>> callback) {
        vertx.eventBus().send("mindMaps.persistor"
                , new JsonObject(config.toMap())
                    .putString("action", action)
                    .putString("collection", "mindMaps")
                , callback
        );
    }
}
