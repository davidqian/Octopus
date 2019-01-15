package com.my.Octopus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.my.war.common.log.CastleInfoLogger;

/**
 * @author davidqian 2017/07/24
 */
public class StatisticsUtil {
    private static Logger logger = LoggerFactory
            .getLogger(StatisticsUtil.class);
    public static final String GATEWAY = "Gateway";
    public static final String GVGLOGIC = "GVGLogic";
    public static final String GVGNET = "GVGNet";
    public static final String GATEWAY_CLIENT = "GatewayClient";
    public static final String GVG_CASTLE_INFO = "GVG_CASTLE_INFO";

    public static void doStatisticsLog(String info) {
        logger.info(info);
    }

    public static void doStatisticsLog(String type, String name, int size) {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(" ").append(TimeUtil.getTimeStampBasedOnSecond())
                .append(" ").append(name).append(" ").append(size);
        logger.info(sb.toString());
    }

    public static void doStatisticsLog(String type, String name, long size) {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(" ").append(TimeUtil.getTimeStampBasedOnSecond())
                .append(" ").append(name).append(" ").append(size);
        logger.info(sb.toString());
    }

    public static void doStatisticsLog(String type, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(" ").append(TimeUtil.getTimeStampBasedOnSecond())
                .append(" ").append(value);
        CastleInfoLogger.logger.info(sb.toString());
    }

}
