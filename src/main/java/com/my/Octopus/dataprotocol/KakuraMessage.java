package com.my.Octopus.dataprotocol;

import java.util.ArrayList;
import java.util.List;

import com.my.Octopus.compress.SnappyCompress;
import com.my.Octopus.encrypt.Aes;

public class KakuraMessage {
    public int messageType = 0;
    public byte[] buffer = null;
    public String aesKey = "";
    public static int phpResponse = 199999;
    public static int kakuraResponse = 99998;
    public static int kakuraPushMessage = 1010;
    public static int kakuraNoResponsePushMessage = 1016;
    public static int kakuraBackendRequest = 100001;

    public static int KAKURAFIXEDMESSAGE = 1;
    public static int KAKURADYNAMICMESSAGE = 2;

    public ArrayList<KakuraPackage> kakuraPackages = new ArrayList<KakuraPackage>();

    public KakuraMessage(byte[] pBuffer, String aesKey) {
        messageType = (int) pBuffer[0];
        buffer = KakuraPackage.catBytes(pBuffer, 1, pBuffer.length - 1);
        this.aesKey = aesKey;
    }

    public void addPackage(KakuraPackage kakuraPackage) {
        kakuraPackages.add(kakuraPackage);
    }

    public void decodeMessage() {
        byte[] uncomedBuff = null;
        if (messageType == 1 || messageType == 2) {
            Aes aes = new Aes();
            byte[] aesBuff = aes.decrypt(buffer, aesKey);
            uncomedBuff = SnappyCompress.uncompress(aesBuff);
        }

        if (messageType == 32) {
            uncomedBuff = buffer;
        }

        if (uncomedBuff == null) {
            return;
        }

        int offset = 0;
        int buffLen = uncomedBuff.length;
        while (offset < buffLen) {
            KakuraPackage kakuraPackage = KakuraPackage.decode(uncomedBuff, offset);
            kakuraPackages.add(kakuraPackage);
            offset += 13 + kakuraPackage.uniquerequestIdLen + kakuraPackage.len;
        }
    }

    public List<KakuraPackage> getPackages() {
        if (kakuraPackages.size() == 0) {
            decodeMessage();
        }
        return kakuraPackages;
    }

    public static byte[] createMessage(int messageType, KakuraPackage kakuraPackage, String aesKey) throws Exception {
        byte[] head = new byte[1];
        head[0] = (byte) messageType;

        byte[] outBuff = null;
        byte[] p = kakuraPackage.encode();

        if (messageType == 1 || messageType == 2) {
            Aes aes = new Aes();
            byte[] snappyCompressed = SnappyCompress.compress(p);
            outBuff = aes.encrypt(snappyCompressed, aesKey);
        }

        if (messageType == 32) {
            outBuff = p;
        }

        return Aes.contactBytes(head, outBuff);
    }
}