package com.my.Octopus.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author davidqian 2017/07/25
 */

public class TimeUtil {

    /**
     * 获取当前以秒为单位的时间戳
     *
     * @return
     */
    public static Long getTimeStampBasedOnSecond() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取指定时间对应的毫秒数
     *
     * @param time "HH:mm:ss"
     * @return
     * @throws ParseException
     */
    public static long getTodayTimeMillis(String time) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
        Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
        return curDate.getTime();
    }
}
