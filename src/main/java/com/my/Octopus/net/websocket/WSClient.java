package com.my.Octopus.net.websocket;

import java.util.concurrent.ExecutionException;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by davidqian on 17/7/16.
 */

public class WSClient {

    public static Logger logger = LoggerFactory.getLogger(WSClient.class);

    private WebSocket websocket = null;

    private IWSClient client = null;

    public String uri = null;

    public int status = 0; //状态开关，0表示连接未连接，1表示已经连接上状态

    private static AsyncHttpClientConfig cf = new DefaultAsyncHttpClientConfig.Builder()
            .setRequestTimeout(30000)
            .setConnectTimeout(30000)
            .setWebSocketMaxFrameSize(2621440)
            .build();

    private static AsyncHttpClient websocketClient = new DefaultAsyncHttpClient(cf);

    public void connect(String wsUrl) throws InterruptedException, ExecutionException {
        uri = wsUrl;
        websocketClient.prepareGet(wsUrl).execute(new WebSocketUpgradeHandler.Builder()
                .addWebSocketListener(new WSClientByteHandler(this))
                .build()).get();
    }

    public void setClient(IWSClient client) {
        this.client = client;
    }

    public void setWebSocket(WebSocket websocket) {
        this.websocket = websocket;
    }

    public void setStatus(int status) {
        this.status = status;
        this.client.statusChanged(this);
    }

    public void receiveMessage(byte[] buff) {
        this.client.receiveMessage(this, buff);
    }

    public boolean sendMessage(byte[] msg) {
        if (this.status > 0) {
            long startTime = System.currentTimeMillis();
            this.websocket.sendMessage(msg);
            long timeDiff = System.currentTimeMillis() - startTime;
            if (timeDiff > 10) {
                logger.info("[gatewayclient] send use time {} ms", timeDiff);
            }
            return true;
        }
        return false;
    }

}
