package com.my.Octopus.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.my.Octopus.dataconfig.DataConfigManager;
import com.my.war.gvg.gvgLogic.config.ServerConfig;

public class VersionManager {

    private final static VersionManager instance = new VersionManager();

    public Logger logger = LoggerFactory.getLogger(VersionManager.class);

    private Map<String, String> map = new HashMap<>();

    private String config_tag_key = "config_tag";

    private String script_tag_key = "script_tag";

    public VersionManager() {
        //init();
    }

    public static VersionManager getInstance() {
        return instance;
    }

    public void init() {
        String path = ServerConfig.instance.getOnlineVersion();
        try {
            String cfgText = FileUtils.getStringFromFile(path);

            int[] info = new Gson().fromJson(cfgText, int[].class);

            int version = this.getMax(info);

            StringBuilder sb = new StringBuilder();

            sb.append(ServerConfig.instance.getVersionPath()).append(version).append("/info.yaml");

            String versionPath = sb.toString();

            this.read(versionPath);

            logger.info("[VersionManager] param config_tag:{}", this.getConfigVersion());
            logger.info("[VersionManager] param script_tag:{}", this.getScriptVersion());

            this.initDataConfig();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(-1);
        }

    }

    private void initDataConfig() {
        StringBuilder sb = new StringBuilder();
        sb.append(ServerConfig.instance.getDataConfigPath())
                .append(this.getConfigVersion()).append("/");
        String configPath = sb.toString();
        logger.info("configPath:{}", configPath);
        DataConfigManager.dataConfigPath = configPath;
        DataConfigManager.initAllConfig("com.my.war.gvg.gvgLogic.config");
    }

    private void read(String path) {

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
        try {
            String str = "";

            fis = new FileInputStream(path);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                String vs[] = str.split(":");

                if (vs.length != 2) {
                    continue;
                }

                map.put(vs[0], vs[1].replace("\'", "").trim());
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(-1);
        } finally {
            try {
                br.close();
                isr.close();
                fis.close();
                // 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private int getMax(int vs[]) {
        int value = vs[0];
        int length = vs.length;

        for (int i = 1; i < length; i++) {
            if (value < vs[i]) {
                value = vs[i];
            }
        }

        return value;
    }

    public String getConfigVersion() {
        return map.get(this.config_tag_key);
    }


    public String getScriptVersion() {
        return map.get(this.script_tag_key);
    }

    public String getEcho() {
        return "test_echo";
    }

    public String getType() {
        return "";
    }


}
