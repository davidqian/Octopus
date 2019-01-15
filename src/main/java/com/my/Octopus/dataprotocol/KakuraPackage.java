package com.my.Octopus.dataprotocol;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.my.Octopus.encrypt.Aes;

public class KakuraPackage {
    public static Logger logger = LoggerFactory.getLogger(KakuraPackage.class);
    public int opCode = 0;
    public int len = 0;
    public int requestId = 0;
    public int uniquerequestIdLen = 0;
    public byte[] uniquerequestId = null;
    public byte[] value = null;

    public byte[] encode() throws UnsupportedEncodingException {
        byte[] opcodeByte = KakuraPackage.intToByteArray(opCode);
        byte[] lenByte = KakuraPackage.intToByteArray(value.length);
        byte[] requestIdByte = KakuraPackage.intToByteArray(requestId);

        byte[] uniqueRequestIdBuff = new byte[1];
        if (uniquerequestId != null) {
            uniquerequestIdLen = uniquerequestId.length;
        }

        uniqueRequestIdBuff[0] = (byte) uniquerequestIdLen;

        byte[] uniqueBuff = null;
        if (uniquerequestIdLen > 0) {
            uniqueBuff = Aes.contactBytes(uniqueRequestIdBuff, uniquerequestId);
        } else {
            uniqueBuff = uniqueRequestIdBuff;
        }

        byte[] s1 = Aes.contactBytes(opcodeByte, lenByte);
        byte[] s2 = Aes.contactBytes(s1, requestIdByte);
        byte[] s3 = Aes.contactBytes(s2, uniqueBuff);
        byte[] ret = Aes.contactBytes(s3, value);
        return ret;
    }

    public static KakuraPackage decode(byte[] buff, int offset) {
        KakuraPackage kakuraPackage = new KakuraPackage();
        kakuraPackage.opCode = KakuraPackage.byteArrayToInt(buff, offset);
        offset += 4;
        kakuraPackage.len = KakuraPackage.byteArrayToInt(buff, offset);
        offset += 4;
        kakuraPackage.requestId = KakuraPackage.byteArrayToInt(buff, offset);
        offset += 4;
        kakuraPackage.uniquerequestIdLen = (int) buff[offset];
        offset += 1;
        if (kakuraPackage.uniquerequestIdLen > 0) {
            kakuraPackage.uniquerequestId = KakuraPackage.catBytes(buff, offset, kakuraPackage.uniquerequestIdLen);
            offset += kakuraPackage.uniquerequestIdLen;
        }

        kakuraPackage.value = KakuraPackage.catBytes(buff, offset, kakuraPackage.len);
        return kakuraPackage;
    }

    public static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 3; i >= 0; i--) {
            int shift = i * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    public static byte[] intToByteArray(int integer) {
        byte[] byteArray = new byte[4];
        for (int n = 0; n < 4; n++) {
            byteArray[n] = (byte) (integer >> (n * 8));
        }
        return (byteArray);
    }

    public static byte[] catBytes(byte[] buf, int start, int len) {
        byte[] bs = new byte[len];
        for (int i = start; i < start + len; i++) {
            bs[i - start] = buf[i];
        }
        return bs;
    }
}
