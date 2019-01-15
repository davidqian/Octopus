package com.my.war.match2P.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.my.war.match2P.config.ServerConfig;
import com.my.war.match2P.user.UserData;

public class CallBackThread implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(CallBackThread.class);
    public ExecutorService fixThreadPool = Executors.newFixedThreadPool(ServerConfig.instance.getCallbackThreadCount());

    public void run() {
        while (true) {
            try {
                UserData data = ThreadQueue.getInstance().takeCallbackQueue();
                fixThreadPool.execute(new CallbackClientThread(data));
            } catch (InterruptedException e) {
                logger.error("get callbackqueue error:{}", e.getMessage());
            }
        }
    }

}
