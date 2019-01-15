package com.my.Octopus.net.http;

import com.my.Octopus.net.Dispatch;
import com.my.Octopus.net.Session;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.LastHttpContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;

/**
 * Created by davidqian on 2017/7/17.
 */
public class LogServerHandler extends SimpleChannelInboundHandler<Object> {

    private static Logger logger = LoggerFactory.getLogger(LogServerHandler.class);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

        Session session = new Session(Session.session_type.HTTP_SESSION);
        session.setCtx(ctx);
        if (msg instanceof LastHttpContent) {
            try {
                LastHttpContent httpContent = (LastHttpContent) msg;
                ByteBuf content = httpContent.content();

                byte[] bytes = new byte[content.readableBytes()];
                content.readBytes(bytes);

                Dispatch.instance.dispatchLogServerMsg(session, bytes);
            } catch (Exception e) {
                String err = "{\"result\":null,\"error\":\"logic error\"}";
                session.writeHttp(BAD_REQUEST, err);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exceptionCaught : " + cause);
        ctx.close();
    }

}
