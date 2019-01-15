package com.my.Octopus.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.my.Octopus.util.GZipUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.my.Octopus.gatewaylayer.GatewaySession;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidqian on 17/7/16.
 */

public class Dispatch {
    private static Logger logger = LoggerFactory.getLogger(Dispatch.class);
    public static Dispatch instance = new Dispatch();

    private MsgDispatch dispatch = null;

    public void initDispatch(String proDispatchClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.dispatch = (MsgDispatch) Class.forName(proDispatchClass).newInstance();
    }

    public void dispatchMsg(GatewaySession session, byte[] req) {
        if (dispatch != null) {
            try {
                MethodUtils.invokeMethod(dispatch, "dispatch", new Object[]{session, req});
            } catch (Exception e) {
                logger.error("dispatch error:{},data:{}", e.getMessage(), new String(req));
            }
        } else {
            logger.error("dispatch not inited!");
        }
    }

    public void dispatchMsg(GatewaySession session, String req) {
        if (dispatch != null) {
            try {
                MethodUtils.invokeMethod(dispatch, "dispatch", new Object[]{session, req});
            } catch (Exception e) {
                logger.error("dispatch error:{},data:{}", e.getMessage(), new String(req));
            }
        } else {
            logger.error("dispatch not inited!");
        }
    }

    /**
     * gateway 发现目标机器不存在，返回错误
     *
     * @param err 错误码
     * @param req 发送的数据
     */
    public void dispatchMsg(int err, byte[] req) {
        if (dispatch != null) {
            try {
                MethodUtils.invokeMethod(dispatch, "dispatch", new Object[]{err, req});
            } catch (Exception e) {
                logger.error("dispatch error:{},data:{}", e.getMessage(), new String(req));
            }
        } else {
            logger.error("dispatch not inited!");
        }
    }

    public void dispatchMsg(Session session, byte[] req) {
        if (dispatch != null) {
            try {
                MethodUtils.invokeMethod(dispatch, "dispatch", new Object[]{session, req});
            } catch (Exception e) {
                logger.error("dispatch error:{},data:{}", e.getMessage(), new String(req), e);
            }
        } else {
            logger.error("dispatch not inited!");
        }
    }

    public void dispatchMsg(Session session, String req) {
        if (dispatch != null) {
            try {
                MethodUtils.invokeMethod(dispatch, "dispatch", new Object[]{session, req});
            } catch (Exception e) {
                logger.error("dispatch error:{},data:{}", e.getMessage(), new String(req));
            }
        } else {
            logger.error("dispatch not inited!");
        }
    }

    public void octopusDispatch(Session session, int code) {
        if (dispatch != null) {
            try {
                MethodUtils.invokeMethod(dispatch, "octopusDispatch", new Object[]{session, code});
            } catch (Exception e) {
                logger.error("dispatch error:{},data:{}", e.getMessage(), code);
            }
        } else {
            logger.error("dispatch not inited!");
        }
    }

    public void dispatchLogServerMsg(Session session, byte[] req) {
        String retStr = "{\"result\":\"OK\",\"error\":null}";
        try {
            String postData = new String(req, "utf-8");
            JSONObject rootObj = JSONObject.parseObject(postData);

            String method = rootObj.getString("method");
            JSONObject paramObj = rootObj.getJSONObject("params");

            if (method == null || method.trim().length() == 0) {
                retStr = "{\"result\":null,\"error\":\"params error\"}";
                session.writeHttp(HttpResponseStatus.BAD_REQUEST, retStr);
                return;
            }

            if (method.equals("fileList")) {
//				String logPath = System.getProperties().getProperty("log.home");
                String logPath = paramObj.getString("path");
                File file = new File(logPath);
                File[] files = file.listFiles();

                List<String> fileNames = new ArrayList<>();
                for (File f : files) {
                    fileNames.add(f.getPath());
                }
                session.writeHttp(HttpResponseStatus.OK, JSON.toJSONString(fileNames));
            } else if (method.equals("file")) {
                String path = paramObj.getString("path");
                File file = new File(path);
                if (!file.exists()) {
                    retStr = "{\"result\":null,\"error\":\"Can not find file\"}";
                    session.writeHttp(HttpResponseStatus.BAD_REQUEST, retStr);
                    return;
                }

                byte[] bytes = Files.readAllBytes(file.toPath());
                bytes = GZipUtils.compress(bytes);
                ByteBuf buffer = Unpooled.copiedBuffer(bytes);
                MimetypesFileTypeMap mimeTypeMap = new MimetypesFileTypeMap();
                String contentType = mimeTypeMap.getContentType(file);

                session.writeFile(HttpResponseStatus.OK, contentType, buffer);
            } else {
                retStr = "{\"result\":null,\"error\":\"Can not find method:" + method + "\"}";
                session.writeHttp(HttpResponseStatus.BAD_REQUEST, retStr);
            }

        } catch (Exception ex) {
            logger.error("dispatch error:{}", ex.getMessage(), ex);
            retStr = "{\"result\":null,\"error\":\"dispatch error\"}";
            session.writeHttp(HttpResponseStatus.BAD_REQUEST, retStr);
        }
    }
}
