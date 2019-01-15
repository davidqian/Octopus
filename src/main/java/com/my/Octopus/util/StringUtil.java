package com.my.Octopus.util;

import java.util.UUID;

/**
 * 字符串工具
 *
 * @author davidqian
 */
public class StringUtil {

    public static String toLowerCaseFirstLetter(String src) {
        char[] cs = src.toCharArray();
        if (cs[0] >= 'A' && cs[0] <= 'Z') {
            cs[0] += 32;
        }
        return String.valueOf(cs);
    }

    public static String getUUid() {
        return UUID.randomUUID().toString();
    }

    public static Long ipToLong(String ip) {
        Long ips = 0L;
        String[] numbers = ip.split("\\.");
        for (int i = 0; i < 4; ++i) {
            ips = ips << 8 | Integer.parseInt(numbers[i]);
        }
        return ips;
    }

    public static String Long2Ip(long ip) {
        int[] ipNum = new int[4];
        for (int i = 0; i < 4; i++) {
            ipNum[i] = (int) (ip & 255);
            ip = ip >> 8;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ipNum[3]).append(".").append(ipNum[2]).append(".").append(ipNum[1]).append(".").append(ipNum[0]);
        return sb.toString();
    }

    public static boolean isEmpty(String str) {
        return !(str != null && str.trim().length() > 0);
    }

}
