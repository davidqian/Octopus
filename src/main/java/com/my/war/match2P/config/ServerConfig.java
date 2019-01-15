package com.my.war.match2P.config;

import java.util.ArrayList;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.my.Octopus.util.FileUtils;

public class ServerConfig {
    public static Logger logger = LoggerFactory.getLogger(ServerConfig.class);

    public static ServerConfig instance = null;

    private String httpPort = "8888";

    private String dataConfigPath = "/data/home/user00/my/war/version/config/";

    private int threadCount = 4;

    private int callbackThreadCount = 1;

    private int pollNumOneTime = 500;

    private String callbackUrl = "http://localhost:8000/index.php?act=test.index";

    private String logbackXml = System.getProperty("server.home") + "/config/logback.xml";

    private ArrayList<String> hdServers = null;

    private String zkHost = null;

    private int zkSessionTimeOut = 86400;

    private String zkParentPath = "heroduel";

    private JSONObject redis = null;

    public String getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(String httpPort) {
        this.httpPort = httpPort;
    }

    public String getDataConfigPath() {
        return dataConfigPath;
    }

    public void setDataConfigPath(String dataConfigPath) {
        this.dataConfigPath = dataConfigPath;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public static boolean load(String cfgPath) {
        try {
            String cfgText = FileUtils.getStringFromFile(cfgPath);
            instance = JSON.parseObject(cfgText, ServerConfig.class);
            return true;
        } catch (Exception e) {
            logger.error("load config error, exception:{}", e);
            return false;
        }
    }

    public int getPollNumOneTime() {
        return pollNumOneTime;
    }

    public void setPollNumOneTime(int pollNumOneTime) {
        this.pollNumOneTime = pollNumOneTime;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getLogbackXml() {
        return logbackXml;
    }

    public void setLogbackXml(String logbackXml) {
        this.logbackXml = logbackXml;
    }

    public String getZkHost() {
        return zkHost;
    }

    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }

    public int getZkSessionTimeOut() {
        return zkSessionTimeOut;
    }

    public void setZkSessionTimeOut(int zkSessionTimeOut) {
        this.zkSessionTimeOut = zkSessionTimeOut;
    }

    public String getZkParentPath() {
        return zkParentPath;
    }

    public void setZkParentPath(String zkParentPath) {
        this.zkParentPath = zkParentPath;
    }

    public ArrayList<String> getHdServers() {
        return hdServers;
    }

    public void setHdServers(ArrayList<String> hdServers) {
        this.hdServers = hdServers;
    }

    public String randHDServer() {
        int len = this.hdServers.size();
        Random rand = new Random(len);
        int index = rand.nextInt(len);
        return this.hdServers.get(index);
    }

    public int getCallbackThreadCount() {
        return callbackThreadCount;
    }

    public void setCallbackThreadCount(int callbackThreadCount) {
        this.callbackThreadCount = callbackThreadCount;
    }

    public JSONObject getRedis() {
        return redis;
    }

    public void setRedis(JSONObject redis) {
        this.redis = redis;
    }
}
