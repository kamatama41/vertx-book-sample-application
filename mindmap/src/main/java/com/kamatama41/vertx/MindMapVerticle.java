package com.kamatama41.vertx;

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.impl.Json;
import org.vertx.java.platform.Verticle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MindMapVerticle extends Verticle {

    private static final Map<Integer, MindMap> mindMaps = new HashMap<>();
    private static final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void start() {
        vertx.eventBus().registerHandler("mindMaps.list", event -> {
            Map<String, Collection<MindMap>> result = new HashMap<>();
            result.put("mindMaps", mindMaps.values());
            event.reply(new JsonObject(Json.encode(result)));
        });

        vertx.eventBus().registerHandler("mindMaps.save", (Message<JsonObject> event) -> {
            MindMap mindMap = Json.decodeValue(event.body().toString(), MindMap.class);
            if (mindMap.getId() == null) {
                mindMap = new MindMap(counter.incrementAndGet(), mindMap.getName());
            }

            mindMaps.put(mindMap.getId(), mindMap);
            event.reply(new JsonObject(Json.encode(mindMap)));
        });

        vertx.eventBus().registerHandler("mindMaps.delete", (Message<JsonObject> event) -> {
            final MindMap mindMap = Json.decodeValue(event.body().toString(), MindMap.class);
            mindMaps.remove(mindMap.getId());
            event.reply(new JsonObject());
        });

    }
}
