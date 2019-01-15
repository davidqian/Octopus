package com.my.Octopus.dataprotocol;

import com.my.Octopus.util.Codes;

import java.nio.ByteBuffer;

/**
 * Created by davidqian on 6/17/17.
 */
public class GateMessage {
    private int type = 0;
    private byte[] value = null;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value.getBytes();
    }

    public byte[] encode() {
        int bufferLen = 4 + 4;//type + valuelen

        if (value != null) {
            bufferLen += value.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(bufferLen);
        buffer.putInt(this.type);
        if (value != null) {
            buffer.putInt(this.value.length);
            buffer.put(this.value);
        } else {
            buffer.putInt(0);
        }

        buffer.flip();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        buffer.clear();

        return bytes;
    }

    public static GateMessage decode(byte[] bytes) {
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        GateMessage gm = new GateMessage();
        gm.setType(buf.getInt());
        int valueLength = buf.getInt();
        if (valueLength != 0) {
            byte[] valueBytes = new byte[valueLength];
            buf.get(valueBytes);
            gm.value = valueBytes;
        }

        buf.clear();
        return gm;
    }
}
