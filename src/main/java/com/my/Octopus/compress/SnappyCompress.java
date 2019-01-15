package com.my.Octopus.compress;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;

public class SnappyCompress {

    public static Logger logger = LoggerFactory.getLogger(SnappyCompress.class);


    public static byte[] compress(byte[] msg) throws IOException {
        try {
            return Snappy.compress(msg);
        } catch (IOException e) {
            logger.error("SnappyCompress compress error:", e);
        }
        return null;
    }

    public static byte[] uncompress(byte[] srcBytes) {
        try {
            return Snappy.uncompress(srcBytes);
        } catch (IOException e) {
            logger.error("SnappyCompress uncompress error:", e);
        }
        return null;
    }
}