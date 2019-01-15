package com.my.Octopus.tlog;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.my.Octopus.net.http.HttpClientUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by davidqian on 2017/8/25.
 */
public class TLogManager {

    private static Logger logger = LoggerFactory.getLogger(TLogManager.class);

    private static TLogManager instance = new TLogManager();

    public static TLogManager getInstance() {
        return instance;
    }

    private LinkedBlockingQueue<TLogModel> tLogQueue = new LinkedBlockingQueue<>();

    public TLogManager() {
        Thread th = new Thread(new TLogThread());
        th.setUncaughtExceptionHandler(new TLogThreadUncatchExceptionHandler());
        th.start();
    }

    public void sendTLog(TLogModel tLog) {
        this.tLogQueue.add(tLog);
    }

    private class TLogThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    TLogModel tLogModel = tLogQueue.take();

                    List<NameValuePair> params = Lists.newArrayList();
                    params.add(new BasicNameValuePair("table", tLogModel.getTable()));
                    params.add(new BasicNameValuePair("params", new Gson().toJson(tLogModel.getParams())));
                    params.add(new BasicNameValuePair("pGroup", tLogModel.getPlatform()));
                    params.add(new BasicNameValuePair("sec", tLogModel.getSec()));
                    String s = HttpClientUtil.doPost(tLogModel.getUrl(), params);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private class TLogThreadUncatchExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            Thread th = new Thread(new TLogThread());
            th.setUncaughtExceptionHandler(new TLogThreadUncatchExceptionHandler());
            th.start();
            logger.error(e.getMessage(), e);
        }
    }
}
