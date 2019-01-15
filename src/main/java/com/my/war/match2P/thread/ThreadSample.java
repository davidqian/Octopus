package com.my.war.match2P.thread;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.my.war.match2P.config.ServerConfig;

public class ThreadSample {
    public static void initMatchThread() {
        int threadCount = ServerConfig.instance.getThreadCount();
        for (int i = 0; i < threadCount; i++) {
            Thread mth = new Thread(new MatchThread(i));
            ThreadUncatchExceptionHandler.threadMap.put(mth.getId(), i);
            mth.start();
        }
    }

    public static void initCallbackThread() {
        new Thread(new CallBackThread()).start();
    }
}

class ThreadUncatchExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(ThreadUncatchExceptionHandler.class);

    public static ConcurrentHashMap<Long, Integer> threadMap = new ConcurrentHashMap<Long, Integer>();

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        long threadId = t.getId();
        int num = threadMap.get(threadId);
        if (num >= 0) {
            Thread mth = new Thread(new MatchThread(num));
            ThreadUncatchExceptionHandler.threadMap.put(mth.getId(), num);
            mth.start();
        }
        threadMap.remove(threadId);
        logger.error("ThreadUncatchExceptionHandler " + e);
    }

}
