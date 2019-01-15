package com.my.Octopus.launcher;

import bsh.servlet.BshServlet;
import com.my.Octopus.jettyservlet.FileListServlet;
import com.my.Octopus.jettyservlet.FileServlet;
import com.my.Octopus.net.Dispatch;
import com.my.Octopus.net.Session;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.LastHttpContent;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;

/**
 * Created by davidqian on 2017/7/17.
 */
public abstract class Launcher {
    private static Logger logger = null;

    public abstract void launcher(String[] args);

    public void initJettyHttpServer(int port) throws Exception {
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new BshServlet()), "/bsh");
        context.addServlet(new ServletHolder(new FileListServlet()), "/filelist");
        context.addServlet(new ServletHolder(new FileServlet()), "/file");
        server.setHandler(context);
        server.start();
    }

    public void initLogServer(int port) {
        logger = LoggerFactory.getLogger(Launcher.class);

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();

                    p.addLast("decoder", new HttpRequestDecoder());
                    p.addLast("aggregator", new HttpObjectAggregator(10240));
                    p.addLast("encoder", new HttpResponseEncoder());
                    p.addLast("handler", new SimpleChannelInboundHandler<Object>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
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
                    });
                }
            });

            Channel channel = bootstrap.bind(port).sync().channel();
            logger.info("logServer启动成功，端口号:{}", port);
            channel.closeFuture().sync();
        } catch (Exception e) {
            logger.info("logServer启动失败，端口号:{}被占用", port);
        } finally {
            workGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
