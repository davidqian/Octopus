package com.my.Octopus.net.websocket;

import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketByteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by davidqian on 17/7/16.
 */

public class WSClientByteHandler implements WebSocketByteListener {
    private static Logger logger = LoggerFactory.getLogger(WSClientByteHandler.class);

    private WSClient wsClient = null;

    public WSClientByteHandler(WSClient wsClient) {
        this.wsClient = wsClient;
    }

    @Override
    public void onOpen(WebSocket websocket) {
        wsClient.setWebSocket(websocket);
        wsClient.setStatus(1);
    }

    @Override
    public void onClose(WebSocket websocket) {
        wsClient.setStatus(0);
        logger.error("websocket client closed");
    }

    @Override
    public void onError(Throwable t) {
        wsClient.setStatus(0);
        logger.error("[websocketClient] error:{}", t);
    }

    @Override
    public void onMessage(byte[] message) {
        wsClient.receiveMessage(message);
    }

}
