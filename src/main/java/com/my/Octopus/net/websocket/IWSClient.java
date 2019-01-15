package com.my.Octopus.net.websocket;

/**
 * Created by davidqian on 17/7/16.
 */
public abstract class IWSClient {
    public abstract void statusChanged(WSClient wsClient);

    public abstract void receiveMessage(WSClient wsClient, byte[] buf);
}