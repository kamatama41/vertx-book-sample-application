package com.kamatama41.vertx;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;
import org.vertx.java.platform.impl.cli.Starter;

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
    }

    public static void main(String[] args) {
        // Convert Log4j -> SLF4j
        System.setProperty("org.vertx.logger-delegate-factory-class-name", "org.vertx.java.core.logging.impl.SLF4JLogDelegateFactory");

        // Run vert.x
        Starter.main(new String[]{
                "run", App.class.getCanonicalName()
        });
    }
}
