package com.my.Octopus.net.http;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.my.Octopus.net.INetIO;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Http服务器
 *
 * @author davidqian
 */
public class HttpServer implements INetIO {

    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public void start(String host, int listenPort) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new HttpServerInitializer());

            Channel channel = bootstrap.bind(listenPort).sync().channel();
            channel.closeFuture().sync();

        } catch (InterruptedException e) {
            logger.error("http start error " + e.getStackTrace().toString());
        } finally {
            workGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public void stop() {

    }
}
