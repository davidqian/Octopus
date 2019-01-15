package com.my.Octopus.net.websocket;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.my.Octopus.net.Dispatch;
import com.my.Octopus.net.Session;
import com.my.Octopus.net.SessionManager;
import com.my.Octopus.util.Codes;

/**
 * Created by davidqian on 17/7/16.
 */

public class WSInitializer extends ChannelInitializer<SocketChannel> {

    private static Logger logger = LoggerFactory.getLogger(WSInitializer.class);

    /**
     * 初始化ChannelPipeline(注册请求处理对象)
     */

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast("idleStateHandler", new IdleStateHandler(180, 0, 0, TimeUnit.SECONDS));// 180秒不操作将会被断开
        pipeline.addLast("idleHandler", new IdleHandler());
        pipeline.addLast(new WSHandler());
    }

    static class IdleHandler extends ChannelHandlerAdapter {
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                if (e.state() == IdleState.READER_IDLE) {
                    ctx.close();// 关闭连接
                    Session session = Session.getSession(ctx);
                    if (session != null) {
                        SessionManager.removeSession(session);
                        Dispatch.instance.octopusDispatch(session, Codes.NETCLOSED);
                    }
                    logger.error("Session idle close");
                }
            }
        }
    }
}
