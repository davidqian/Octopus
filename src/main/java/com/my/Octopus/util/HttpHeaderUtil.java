package com.my.Octopus.util;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;

import java.util.Iterator;
import java.util.List;

/**
 * Created by davidqian on 2017/6/20.
 */
public class HttpHeaderUtil {
    public static boolean isKeepAlive(HttpMessage message) {
        CharSequence connection = (CharSequence) message.headers().get(HttpHeaderNames.CONNECTION);
        return connection != null && HttpHeaderValues.CLOSE.contentEqualsIgnoreCase(connection) ? false : (message.protocolVersion().isKeepAliveDefault() ? !HttpHeaderValues.CLOSE.contentEqualsIgnoreCase(connection) : HttpHeaderValues.KEEP_ALIVE.contentEqualsIgnoreCase(connection));
    }

    public static void setKeepAlive(HttpMessage message, boolean keepAlive) {
        HttpHeaders h = message.headers();
        if (message.protocolVersion().isKeepAliveDefault()) {
            if (keepAlive) {
                h.remove(HttpHeaderNames.CONNECTION);
            } else {
                h.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            }
        } else if (keepAlive) {
            h.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        } else {
            h.remove(HttpHeaderNames.CONNECTION);
        }

    }

    public static long getContentLength(HttpMessage message) {
        Long value = Long.parseLong(message.headers().get(HttpHeaderNames.CONTENT_LENGTH));
        if (value != null) {
            return value.longValue();
        } else {
            long webSocketContentLength = (long) getWebSocketContentLength(message);
            if (webSocketContentLength >= 0L) {
                return webSocketContentLength;
            } else {
                throw new NumberFormatException("header not found: " + HttpHeaderNames.CONTENT_LENGTH);
            }
        }
    }

    public static long getContentLength(HttpMessage message, long defaultValue) {
        Long value = Long.parseLong(message.headers().get(HttpHeaderNames.CONTENT_LENGTH));
        if (value != null) {
            return value.longValue();
        } else {
            long webSocketContentLength = (long) getWebSocketContentLength(message);
            return webSocketContentLength >= 0L ? webSocketContentLength : defaultValue;
        }
    }

    private static int getWebSocketContentLength(HttpMessage message) {
        HttpHeaders h = message.headers();
        if (message instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) message;
            if (HttpMethod.GET.equals(req.method()) && h.contains(HttpHeaderNames.SEC_WEBSOCKET_KEY1) && h.contains(HttpHeaderNames.SEC_WEBSOCKET_KEY2)) {
                return 8;
            }
        } else if (message instanceof HttpResponse) {
            HttpResponse res = (HttpResponse) message;
            if (res.status().code() == 101 && h.contains(HttpHeaderNames.SEC_WEBSOCKET_ORIGIN) && h.contains(HttpHeaderNames.SEC_WEBSOCKET_LOCATION)) {
                return 16;
            }
        }

        return -1;
    }

    public static void setContentLength(HttpMessage message, long length) {
        message.headers().set(HttpHeaderNames.CONTENT_LENGTH, length);
    }

    public static boolean isContentLengthSet(HttpMessage m) {
        return m.headers().contains(HttpHeaderNames.CONTENT_LENGTH);
    }

    public static boolean is100ContinueExpected(HttpMessage message) {
        if (!(message instanceof HttpRequest)) {
            return false;
        } else if (message.protocolVersion().compareTo(HttpVersion.HTTP_1_1) < 0) {
            return false;
        } else {
            CharSequence value = (CharSequence) message.headers().get(HttpHeaderNames.EXPECT);
            return value == null ? false : (HttpHeaderValues.CONTINUE.contentEqualsIgnoreCase(value) ? true : message.headers().contains(HttpHeaderNames.EXPECT, HttpHeaderValues.CONTINUE, true));
        }
    }

    public static void set100ContinueExpected(HttpMessage message, boolean expected) {
        if (expected) {
            message.headers().set(HttpHeaderNames.EXPECT, HttpHeaderValues.CONTINUE);
        } else {
            message.headers().remove(HttpHeaderNames.EXPECT);
        }

    }

    public static boolean isTransferEncodingChunked(HttpMessage message) {
        return message.headers().contains(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED, true);
    }

    public static void setTransferEncodingChunked(HttpMessage m, boolean chunked) {
        if (chunked) {
            m.headers().add(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
            m.headers().remove(HttpHeaderNames.CONTENT_LENGTH);
        } else {
            List<String> values = m.headers().getAll(HttpHeaderNames.TRANSFER_ENCODING);
            if (values.isEmpty()) {
                return;
            }

            Iterator valuesIt = values.iterator();

            while (valuesIt.hasNext()) {
                CharSequence value = (CharSequence) valuesIt.next();
                if (HttpHeaderValues.CHUNKED.contentEqualsIgnoreCase(value)) {
                    valuesIt.remove();
                }
            }

            if (values.isEmpty()) {
                m.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
            } else {
                m.headers().set(HttpHeaderNames.TRANSFER_ENCODING, values);
            }
        }

    }

    static void encodeAscii0(CharSequence seq, ByteBuf buf) {
        int length = seq.length();

        for (int i = 0; i < length; ++i) {
            buf.writeByte((byte) seq.charAt(i));
        }

    }

}
