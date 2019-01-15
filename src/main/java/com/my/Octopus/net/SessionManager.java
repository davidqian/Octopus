package com.my.Octopus.net;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.my.Octopus.dataprotocol.GateMessage;
import com.my.Octopus.util.Codes;
import com.my.Octopus.util.StatisticsUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {
    private static Logger logger = LoggerFactory.getLogger(SessionManager.class);
    /**
     * key:玩家ID value:会话
     */
    private static ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<String, Session>();

    public static long toNetOver10mill = 0;

    public static Session addSession(Session session) {
        Session old = sessions.putIfAbsent(session.session_id, session);

        return old;
    }

    public static Session getSession(String uniqueId) {
        return sessions.get(uniqueId);
    }

    public static boolean isOnline(String uniqueId) {
        return sessions.get(uniqueId) != null;
    }

    public static void removeSession(Session session) {
        Session ses = sessions.get(session.session_id);
        if (ses != null && ses.session_id.equals(session.session_id)) {
            sessions.remove(ses.session_id);
        }
    }

    public static int getOnlineNum() {
        return sessions.size();
    }

    /**
     * 先不要使用
     *
     * @param msg
     */
    public static void broadcastNet(String msg) {
        GateMessage gm = new GateMessage();
        gm.setType(Codes.GATEWAY_BROADCAST);
        gm.setValue(msg);
        byte[] buf = gm.encode();
        sessions.entrySet().parallelStream().forEach(session -> {
            session.getValue().write(buf);
        });
    }

    public static void sendTo(String msg, int index) {
        int size = sessions.size();
        if (size == 0) {
            logger.error("no session can be use to send message");
            return;
        }

        int choosedIndex = 0;
        if (index == 0 || size > index) {
            choosedIndex = index;
        } else {
            choosedIndex = index % size;
        }
        long startTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, Session>> iterator = sessions.entrySet().iterator();
        int loop = 0;
        boolean send = false;
        while (iterator.hasNext()) {
            if (loop == choosedIndex) {
                Session session = iterator.next().getValue();
                GateMessage gm = new GateMessage();
                gm.setType(Codes.GATEWAY_SENDTO);
                gm.setValue(msg);
                session.write(gm.encode());
                send = true;
                break;
            }
            loop++;
        }
        long timeDiff = System.currentTimeMillis() - startTime;
        if (timeDiff >= 10) {
            toNetOver10mill += 1;
        }
        if (!send) {
            logger.error("message no send:{}", msg);
        }
    }

    public static void sendTo(Session session, String msg) {
        long startTime = System.currentTimeMillis();

        GateMessage gm = new GateMessage();
        gm.setType(Codes.GATEWAY_SENDTO);
        gm.setValue(msg);
        session.write(gm.encode());

        long timeDiff = System.currentTimeMillis() - startTime;
        if (timeDiff >= 10) {
            toNetOver10mill += 1;
        }
    }

    public static long getToNetOver10mill() {
        long old = toNetOver10mill;
        toNetOver10mill = 0;
        return old;
    }

    public static void onTimer() {
        StatisticsUtil.doStatisticsLog(StatisticsUtil.GVGLOGIC, "toNetOver10mill", getToNetOver10mill());
    }
}
