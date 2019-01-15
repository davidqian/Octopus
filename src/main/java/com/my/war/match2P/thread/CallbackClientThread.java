package com.my.war.match2P.thread;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.my.Octopus.net.http.HttpClientUtil;
import com.my.Octopus.util.StringUtil;
import com.my.war.common.util.Util;
import com.my.war.match2P.config.ServerConfig;
import com.my.war.match2P.user.UserData;

public class CallbackClientThread implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(CallbackClientThread.class);
    private UserData userData;

    public CallbackClientThread(UserData userData) {
        this.userData = userData;
    }

    public void run() {
        List<NameValuePair> params = Lists.newArrayList();
        long now = System.currentTimeMillis();
        String roomId = StringUtil.getUUid();
        params.add(new BasicNameValuePair("id1", userData.getUid()));
        params.add(new BasicNameValuePair("sec", userData.getSec()));
        params.add(new BasicNameValuePair("useTime1", String.valueOf(userData.getUseTime())));
        params.add(new BasicNameValuePair("mtime", String.valueOf(now)));
        if (userData.getStatus() == 2) {
            params.add(new BasicNameValuePair("status", "timeout"));
            ThreadQueue.matchTimeout.incrementAndGet();
            logger.debug("匹配超时的玩家id:{},耗时:{}毫秒", userData.getUid(), userData.getUseTime());
        } else {
            params.add(new BasicNameValuePair("status", "success"));
            params.add(new BasicNameValuePair("roomId", roomId));
            params.add(new BasicNameValuePair("id2", userData.getMatched().getUid()));
            params.add(new BasicNameValuePair("checkKey", String.valueOf(Util.makeMatch2HdCheck(roomId, now))));
            params.add(new BasicNameValuePair("useTime2", String.valueOf(userData.getMatched().getUseTime())));
            ThreadQueue.matchSucess.incrementAndGet();
            logger.debug("成功匹配到的玩家id:{} 和 {},耗时:{}毫秒和{}毫秒", userData.getUid(), userData.getMatched().getUid(), userData.getUseTime(), userData.getMatched().getUseTime());
        }
        String ret = HttpClientUtil.doPost(ServerConfig.instance.getCallbackUrl(), params);
        try {
            JSONObject retObj = JSONObject.parseObject(ret);
            if (retObj.getString("s") != null && retObj.getString("s").equals("OK")) {
                ThreadQueue.matchCallbackOk.incrementAndGet();
            } else {
                ThreadQueue.matchCallbackOther.incrementAndGet();
            }
        } catch (Exception ex) {
            ThreadQueue.matchCallbackOther.incrementAndGet();
        }

        logger.debug("ret:{}", ret);
    }
}
