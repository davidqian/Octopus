package com.my.war.match2P;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.my.Octopus.database.redis.RedisManager;
import com.my.Octopus.dataconfig.DataConfigManager;
import com.my.Octopus.dataconfig.RuntimeConfig;
import com.my.Octopus.net.Dispatch;
import com.my.Octopus.net.http.HttpServer;
import com.my.war.match2P.config.CohRange;
import com.my.war.match2P.config.ServerConfig;
import com.my.war.match2P.thread.ThreadQueue;
import com.my.war.match2P.thread.ThreadSample;


public class App {
    private static Logger logger = null;

    public static void main(String[] args) {
        try {
            RuntimeConfig.instance.init(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        logger = LoggerFactory.getLogger(App.class);

        ServerConfig.load(RuntimeConfig.instance.serverConfig);
        DataConfigManager.dataConfigPath = ServerConfig.instance.getDataConfigPath();
        DataConfigManager.initAllConfig("com.my.war.match2P.config");

        try {
            JSONObject redis = ServerConfig.instance.getRedis();

            RedisManager.getInstance().addRedis(redis.getString("name"), 300, 1000, 3000, redis.getString("host"),
                    redis.getInteger("port"), redis.getString("pwd"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            Dispatch.instance.initDispatch("com.my.war.match2P.msgdispatch.HttpDispacth");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("set dispatch error:{}", e.getMessage());
            System.exit(-1);
        }

        /**
         ZKClient zkClient = new ZKClient(ServerConfig.instance.getZkHost(), ServerConfig.instance.getZkSessionTimeOut(), ServerConfig.instance.getZkParentPath(), "" , ZKClient.ztype.CONSUMER);

         if(!zkClient.run()){
         logger.error("link to zk server error");
         System.exit(-1);
         }
         **/

        ThreadQueue.getInstance().initMap();

        ThreadSample.initCallbackThread();
        ThreadSample.initMatchThread();

        HttpServer server = new HttpServer();
        server.start("0.0.0.0", Integer.valueOf(ServerConfig.instance.getHttpPort()));
        logger.info("match server started!");
    }

}
