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
            sendPersisterEvent("find"
                    , new JsonObject().putObject("matcher", new JsonObject())
                    , response -> {
                        event.reply(new JsonObject().putArray("mindMaps", response.getArray("results")));
                    }
            );
        });

        // Find specific MindMap
        vertx.eventBus().registerHandler("mindMaps.find", (Message<JsonObject> event) -> {
            sendPersisterEvent("find"
                    , new JsonObject().putObject("matcher", new JsonObject().putString("_id", event.body().getString("_id")))
                    , response -> {
                        event.reply(new JsonObject().putArray("mindMaps", response.getArray("results")));
                    }
            );
        });

        // Save MindMap
        vertx.eventBus().registerHandler("mindMaps.save", (Message<JsonObject> event) -> {
            final JsonObject mindMap = event.body();
            sendPersisterEvent("save"
                    , new JsonObject().putObject("document", mindMap)
                    , response -> {
                        mindMap.putString("_id", response.getString("_id"));
                        event.reply(mindMap);
                    }
            );
        });

        // Delete specific MindMap
        vertx.eventBus().registerHandler("mindMaps.delete", (Message<JsonObject> event) -> {
            sendPersisterEvent("delete"
                    , new JsonObject().putObject("matcher", new JsonObject().putString("_id", event.body().getString("_id")))
                    , response -> {
                        event.reply(new JsonObject());
                    }
            );
        });
    }

    private void sendPersisterEvent(String action, JsonObject command, Handler<JsonObject> callback) {
        vertx.eventBus().send("mindMaps.persistor"
                , new JsonObject(command.toMap())
                    .putString("action", action)
                    .putString("collection", "mindMaps")
                , (Message<JsonObject> message) -> {
                    final JsonObject response = message.body();
                    if (response.getString("status").equals("ok")) {
                        callback.handle(response);
                    } else {
                        container.logger().warn(response.getString("message"));
                    }
                }
        );
    }
}
