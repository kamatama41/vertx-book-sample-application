package com.kamatama41.vertx;

import org.vertx.java.platform.Verticle;

import java.util.UUID;

/**
 * @author SHINICHI Ishimura
 */
public class EditorVerticle extends Verticle {
    @Override
    public void start() {
    }

    private MindMap.Node findNodeByKey(MindMap.Node root, String key) {
        if(key.equals(root.getKey())) {
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

    private String newNodeKey() {
        return UUID.randomUUID().toString();
    }

    private void publishMindMapEvent(MindMap mindMap, Object event) {
        vertx.eventBus().publish("mindMaps.event." + mindMap.getId(), event);

    }

}
