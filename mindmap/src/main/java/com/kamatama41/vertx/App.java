package com.kamatama41.vertx;

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;
import org.vertx.java.platform.impl.cli.Starter;

import java.util.concurrent.TimeUnit;

public class App extends Verticle {
    @Override
    public void start() {
        container.deployModule(
                "io.vertx~mod-web-server~2.0.0-final"
                , new JsonObject()
                        .putNumber("port", 8080)
                        .putString("host", "localhost")
                        .putBoolean("bridge", true)
                        .putArray("inbound_permitted", new JsonArray()
                                        .addObject(new JsonObject().putString("address", "mindMaps.list"))
                                        .addObject(new JsonObject().putString("address", "mindMaps.save"))
                                        .addObject(new JsonObject().putString("address", "mindMaps.delete"))
                        )
        );
        container.deployModule(
                "io.vertx~mod-mongo-persistor~2.0.0-final"
                , new JsonObject()
                        .putString("address", "mindMaps.persistor")
                        .putString("db_name", "mind_maps")
        );
        container.deployVerticle(
                MindMapVerticle.class.getCanonicalName()
        );

        // ここから先は不要
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        container.deployVerticle(
                App.class.getCanonicalName() + "$" + ClientApp.class.getSimpleName()
        );
    }

    public static void main(String[] args) {
        // Convert Log4j -> SLF4j
        System.setProperty("org.vertx.logger-delegate-factory-class-name", "org.vertx.java.core.logging.impl.SLF4JLogDelegateFactory");

        // Run vert.x
        Starter.main(new String[]{
                "run", App.class.getCanonicalName()
        });
    }

    public static class ClientApp extends Verticle {
        @Override
        public void start() {
            vertx.eventBus().send("mindMaps.list", new JsonObject(),(Message<JsonObject> message) -> {
                System.out.println(message.body().toString());

                MindMap mindMap = new MindMap(null, "test1");
                vertx.eventBus().send("mindMaps.save", mindMap.toJson(),(Message<JsonObject> saveMessage) -> {
                    System.out.println(saveMessage.body().toString());

                    vertx.eventBus().send("mindMaps.delete", MindMap.fromJson(saveMessage.body()).toJson(),(Message<JsonObject> deleteMessage) -> {
                        System.out.println(deleteMessage.body().toString());
                    });
                });

            });
        }
    }
}
