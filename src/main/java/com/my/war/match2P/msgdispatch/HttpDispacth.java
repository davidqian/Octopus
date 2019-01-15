package com.my.war.match2P.msgdispatch;

import com.my.Octopus.gatewaylayer.GatewaySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.my.Octopus.net.MsgDispatch;
import com.my.Octopus.net.Session;
import com.my.war.match2P.config.ServerConfig;
import com.my.war.match2P.thread.ThreadQueue;
import com.my.war.match2P.user.UserData;

import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpDispacth extends MsgDispatch {
    private static Logger logger = LoggerFactory.getLogger(HttpDispacth.class);

    public void dispatch(Session session, byte[] req) {
        String retStr = "{\"result\":\"OK\",\"error\":null}";
        try {
            String postData = new String(req, "utf-8");
            JSONObject rootObj = JSONObject.parseObject(postData);
            String method = rootObj.getString("method");
            JSONObject paramObj = rootObj.getJSONObject("params");
            String uid = paramObj.getString("uid");
            logger.error("method  : " + method + ",uid:" + uid);
            if (method != null && method.equals("CancelMatch")) {
                UserData userData = ThreadQueue.getInstance().getUserFromUsersMap(uid);
                if (userData == null || userData.setStatus(3)) {
                    session.writeHttp(HttpResponseStatus.OK, retStr);
                    ThreadQueue.matchCancelOk.incrementAndGet();
                } else {
                    logger.error("cancel error : " + uid);
                    retStr = "{\"result\":null,\"error\":\"cancel error\"}";
                    session.writeHttp(HttpResponseStatus.OK, retStr);
                    ThreadQueue.matchCancelFailed.incrementAndGet();
                }
            } else {
                UserData udata = new UserData();
                udata.setUid(uid);
                udata.setSec(paramObj.getString("sec"));
                udata.setScore(paramObj.getIntValue("score"));
                udata.setIndex(paramObj.getIntValue("index"));
                logger.debug("进入匹配的玩家id:{}", paramObj.getString("uid"));

                boolean canIn = ThreadQueue.getInstance().addMatchingUser(udata.getUid());
                if (canIn) {
                    int hash = (int) (udata.getIndex() % ServerConfig.instance.getThreadCount());
                    ThreadQueue.getInstance().addUsersMap(udata);
                    ThreadQueue.getInstance().addIndexLinkQueue(hash, udata.getIndex(), udata);
                    session.writeHttp(HttpResponseStatus.OK, retStr);
                    ThreadQueue.matcheIn.incrementAndGet();
                } else {
                    logger.error("have in the queue : " + udata.getUid());
                    retStr = "{\"result\":null,\"error\":\"user exit error\"}";
                    session.writeHttp(HttpResponseStatus.OK, retStr);
                    ThreadQueue.matcheCanNotIn.incrementAndGet();
                }
            }
        } catch (Exception ex) {
            logger.error("dispatch error:{}", ex);
            retStr = "{\"result\":null,\"error\":\"dispatch error\"}";
            session.writeHttp(HttpResponseStatus.BAD_REQUEST, retStr);
        }
    }

    @Override
    public void dispatch(GatewaySession session, byte[] req) {

    }

    public void octopusDispatch(Session session, int code) {

    }

    @Override
    public void dispatch(int err, byte[] req) {
        // TODO Auto-generated method stub

    }
}
