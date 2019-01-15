package com.my.Octopus.net;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.rtsp.RtspHeaders.Names.CONTENT_TYPE;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.my.Octopus.util.StringUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;

public class Session {
    private static Logger logger = LoggerFactory.getLogger(Session.class);

    public static enum session_type {HTTP_SESSION, WEBSOCKET_SESSION, TCP_SESSION, UDP_SESSION}

    ;

    public static String fixedAesKey = null;

    private static AttributeKey<Session> KEY_SESSION = AttributeKey.valueOf("session.unique_id");

    private session_type type;

    private Object player = null;

    public String session_id = null;

    private String dynamicAesKey = null;

    private ChannelHandlerContext ctx;

    private int maxRequestId = 0;

    private Map<Integer, String> responseCache = new ConcurrentSkipListMap<Integer, String>();

    private ArrayList<String> rooms = new ArrayList<>();

    private Map<String, Object> attr = new HashMap<>();

    private boolean closed = false;

    public String strIp = null;

    public long ip = 0L;

    public int port = 0;

    public int role = -1;

    public int index = 0;

    public Session(session_type type) {
        this.type = type;
        this.session_id = UUID.randomUUID().toString();
        SessionManager.addSession(this);
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        ctx.attr(KEY_SESSION).set(this);

        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        strIp = insocket.getAddress().getHostAddress();
        ip = StringUtil.ipToLong(strIp);
        port = insocket.getPort();
    }

    public boolean write(byte[] msg) {
        if (type == session_type.WEBSOCKET_SESSION) {
            ByteBuf bf = Unpooled.copiedBuffer(msg);
            BinaryWebSocketFrame bWF = new BinaryWebSocketFrame(bf);
            ctx.writeAndFlush(bWF);
            return true;
        }
        return false;
    }

    public boolean writeHttp(HttpResponseStatus status, String msg) {
        try {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HTTP_1_1, status, Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
            response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

            ChannelFuture future = ctx.writeAndFlush(response);
            future.addListener(ChannelFutureListener.CLOSE);
            return true;
        } catch (Exception e) {
            ctx.close();
            logger.error("write http response error:{}", e.getMessage());
        }
        return false;
    }

    public boolean writeFile(HttpResponseStatus status, String contentType, ByteBuf buffer) {
        try {
            FullHttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1, status, buffer);
            resp.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
            ChannelFuture future = ctx.writeAndFlush(resp);
            future.addListener(ChannelFutureListener.CLOSE);
            return true;
        } catch (Exception e) {
            ctx.close();
            logger.error("write http response error:{}", e.getMessage());
        }
        return false;
    }

    public void writeAndCache(int requestId, byte[] msg) throws IOException {
        removeResponseCache(requestId);
        addResponseCache(requestId, msg.toString());
        write(msg);
    }

    public void close() {
        if (type == session_type.WEBSOCKET_SESSION) {
            ctx.close();
        }
    }

    public void addResponseCache(int requestId, String data) {
        if (requestId > maxRequestId) {
            responseCache.put(requestId, data);
            maxRequestId = requestId;
        }
    }

    public void removeResponseCache(int maxId) {
        if (this.responseCache.isEmpty()) return;

        Integer minId = this.responseCache.keySet().iterator().next();

        for (int i = minId; i < maxId; i++) {
            this.responseCache.remove(i);
        }
    }

    public byte[] getResponseCache(int requestId) throws Exception {
        if (requestId > maxRequestId) {
            return null;
        } else {
            String cache = responseCache.get(requestId);
            if (cache != null) {
                return cache.getBytes();
            }
            throw new Exception("error requestId");
        }
    }

    public <T> T getPlayer(Class<T> castType) {
        if (player != null) {
            return castType.cast(player);
        }
        return null;
    }

    public <T> void setPlayer(T player) {
        this.player = player;
    }

    public static Session getSession(ChannelHandlerContext ctx) {
        Object session = ctx.attr(KEY_SESSION).get();
        return (Session) session;
    }

    public ArrayList<String> getRooms() {
        return rooms;
    }

    public void addRoom(String roomId) {
        this.rooms.add(roomId);
    }

    public static String getFixedAesKey() {
        return Session.fixedAesKey;
    }

    public static void setFixedAesKey(String fixedAesKey) {
        Session.fixedAesKey = fixedAesKey;
    }

    public String getDynamicAesKey() {
        return dynamicAesKey;
    }

    public void setDynamicAesKey(String dynamicAesKey) {
        this.dynamicAesKey = dynamicAesKey;
    }

    public String getAesKey() {
        if (dynamicAesKey != null) {
            return dynamicAesKey;
        }
        return Session.fixedAesKey;
    }

    public String getSessionId() {
        return session_id;
    }

    public Object getAttr(String key) {
        return this.attr.get(key);
    }

    public int getIntAttr(String key) {
        return (int) this.attr.get(key);
    }

    public String getStringAttr(String key) {
        Object o = this.attr.get(key);
        if (o == null) {
            return null;
        }
        return String.valueOf(o);
    }

    public void setAttr(String key, Object value) {
        this.attr.put(key, value);
    }

    public String getIpPort() {
        return this.ip + ":" + this.port;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
