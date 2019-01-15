package com.my.Octopus.dataconfig;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuntimeConfig {
    private static Logger logger = null;
    public static RuntimeConfig instance = new RuntimeConfig();

    public RuntimeConfig() {
        if (StringUtils.isEmpty(System.getProperty("server.home"))) {
            System.setProperty("server.home", System.getProperty("user.dir"));
        }
    }

    @Option(name = "-sc", usage = "path of start json config file for start, default file is ./server_config.json", required = true)
    public String serverConfig = System.getProperty("server.home") + "/server_config.json";

    @Option(name = "-log", usage = "path of logback", required = true)
    public String logbackConfig = System.getProperty("server.home") + "/logback.xml";

    @Argument
    private List<String> arguments = new ArrayList<String>();

    public void init(String[] args) throws Exception {
        CmdLineParser parser = new CmdLineParser(this);
        parser.setUsageWidth(80);
        parser.parseArgument(args);
        System.getProperties().setProperty("logback.configurationFile", logbackConfig);
        logger = LoggerFactory.getLogger(RuntimeConfig.class);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Uncaught Exception in thread '" + t.getName() + "'", e);
                logger.error(e.getMessage(), e);
            }
        });
    }
}