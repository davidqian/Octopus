package com.my.war.match2P.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.my.Octopus.util.OctopusUtil;
import com.my.war.match2P.config.ServerConfig;
import com.my.war.match2P.user.UserData;

public class MatchThread implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(MatchThread.class);
    private Map<Integer, ArrayList<UserData>> dataMap = new HashMap<Integer, ArrayList<UserData>>();
    private long todayBegin = OctopusUtil.getStartTime();
    int threadNum = 0;
    private Map<String, String> del = null;
    private int roundNum = 0;

    public MatchThread(int threadNum) {
        this.threadNum = threadNum;
    }

    public void run() {

        long startTime = 0;

        while (true) {
            //每天清理统计数据
            long curTodayBegin = OctopusUtil.getStartTime();
            if (todayBegin != curTodayBegin) {
                todayBegin = curTodayBegin;
                ThreadQueue.resetAtomic();
            }

            roundNum++;
            if (roundNum == 60) {
                ThreadQueue.printAtomic();
                roundNum = 0;
            }

            startTime = System.currentTimeMillis();

            processThreadIndexMap(startTime);

            long sleepTime = System.currentTimeMillis() - startTime;
            if (sleepTime < 1000) {
                sleepTime = 1000 - sleepTime;
            } else {
                sleepTime = 10;
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void processThreadIndexMap(long startTime) {
        ConcurrentHashMap<Integer, ConcurrentLinkedQueue<UserData>> indexLinkeQueueMap = ThreadQueue.getInstance().getIndexMap(threadNum);
        for (Map.Entry<Integer, ConcurrentLinkedQueue<UserData>> indexLinkeQueue : indexLinkeQueueMap.entrySet()) {
            processUserDataLinkQueue(startTime, indexLinkeQueue.getKey(), indexLinkeQueue.getValue());
        }
    }

    private void processUserDataLinkQueue(long startTime, int index, ConcurrentLinkedQueue<UserData> userDataLinkQueue) {
        int pollNum = ServerConfig.instance.getPollNumOneTime();
        ArrayList<UserData> arrayUserData = dataMap.get(index);
        if (arrayUserData == null) {
            arrayUserData = new ArrayList<UserData>();
            dataMap.put(index, arrayUserData);
        }
        while (pollNum > 0) {
            UserData udata = userDataLinkQueue.poll();
            if (udata == null) {
                break;
            }
            arrayUserData.add(udata);
            pollNum--;
        }
        processArrayUserData(startTime, arrayUserData);
    }

    private void processArrayUserData(long startTime, ArrayList<UserData> arrayUserList) {
        if (!arrayUserList.isEmpty()) {
            del = new HashMap<String, String>();
            int size = arrayUserList.size();
            for (int i = 0; i < size; i++) {
                UserData matchUser = arrayUserList.get(i);
                if (matchUser.getStatus() > 0) {
                    addNeedDelUser(matchUser);
                    continue;
                }
                int[] matchUserScoreRange = matchUser.getScoreRange(startTime);
                for (int j = i + 1; j < size; j++) {
                    UserData checkUser = arrayUserList.get(j);
                    if (checkUser.getStatus() > 0) {
                        addNeedDelUser(checkUser);
                        continue;
                    }
                    int[] checkUserScoreRange = checkUser.getScoreRange(startTime);
                    if (matchUserScoreRange[0] <= checkUserScoreRange[1] && checkUserScoreRange[0] <= matchUserScoreRange[1]) {
                        boolean matchRet = matchUser.setStatus(1);
                        if (matchRet) {
                            boolean checkRet = checkUser.setStatus(1);
                            if (checkRet) {
                                matchUser.setMatched(checkUser);
                                matchUser.setUseTime(startTime);
                                checkUser.setUseTime(startTime);
                                addNeedDelUser(matchUser);
                                addNeedDelUser(checkUser);
                                ThreadQueue.getInstance().addCallbackQueue(matchUser);
                            } else {
                                matchUser.setStatus(0);
                            }
                        }
                    }
                }
                if (matchUser.getStatus() == 0 && (startTime - matchUser.getAddTime()) >= 115000) {
                    boolean ret = matchUser.setStatus(2);
                    if (ret) {
                        matchUser.setUseTime(startTime);
                        addNeedDelUser(matchUser);
                        ThreadQueue.getInstance().addCallbackQueue(matchUser);
                    }
                }
            }
            Iterator<UserData> iter = arrayUserList.iterator();
            while (iter.hasNext()) {
                UserData udata = iter.next();
                if (del.get(udata.getUid()) != null) {
                    ThreadQueue.getInstance().lessUsersMap(udata.getUid());
                    ThreadQueue.getInstance().lessMatchingUser(udata.getUid());
                    iter.remove();
                }
            }
        }
    }

    private void addNeedDelUser(UserData user) {
        if (del.get(user.getUid()) == null) {
            del.put(user.getUid(), user.getSec());
        }
    }
}
