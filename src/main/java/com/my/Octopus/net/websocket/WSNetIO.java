package com.my.Octopus.net.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.net.Socket;

import com.my.Octopus.net.INetIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by davidqian on 17/7/16.
 */

public class WSNetIO implements INetIO {

    private static Logger logger = LoggerFactory.getLogger(WSNetIO.class);

    private Channel bossChannel;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    /**
     * 绑定端口
     *
     * @param host
     * @param port
     * @throws Exception
     */
    private static void tryBindPort(String host, int port) throws Exception {
        Socket s = new Socket();
        s.setSoLinger(false, 0);
        s.bind(new InetSocketAddress(host, port));
        s.close();
    }

    /**
     * 检测端口是否被占用
     *
     * @param port
     * @return
     */
    public boolean isUsedPort(int port) {
        try {
            tryBindPort("0.0.0.0", port);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 以netty实现网络通信，不使用tomcat
     */
    public void start(String host, int port) {
        if (isUsedPort(port)) {
            logger.error("端口已经存在监听未启用新的监听:", port);
            System.exit(-1);
            return;
        }
        try {
            //启一个BOSS线程池
            bossGroup = new NioEventLoopGroup(1);
            //启多少个worker处理请求
            workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new WSInitializer())
                    .childOption(ChannelOption.SO_KEEPALIVE, true)// 开启时系统会在连接空闲一定时间后像客户端发送请求确认连接是否有效
                    .childOption(ChannelOption.TCP_NODELAY, true)// 关闭Nagle算法
                    .childOption(ChannelOption.SO_LINGER, 0)// 连接关闭时,尝试把示发送完成的数据继续发送,(等待几秒)
                    .childOption(ChannelOption.SO_SNDBUF, 4096)// 系统sockets发送数据buff的大小
                    .childOption(ChannelOption.SO_RCVBUF, 2048)// ---接收
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)// 使用bytebuf池,默认不使用
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)// 使用bytebuf池,默认不使用
                    .option(ChannelOption.SO_REUSEADDR, true)// 端口重用,如果开启则在上一个进程未关闭情况下也能正常启动
                    .option(ChannelOption.SO_BACKLOG, 10000);// 最大等待连接的connection数量
            workerGroup.setIoRatio(100);// 优先处理网络任务(IOTask)再处理UserTask
            bossChannel = bootstrap.bind(port).sync().channel();
            logger.info("开启监听服务成功,端口：{}", port);
        } catch (Exception e) {
            logger.info("在" + port + "端口开启监听服务失败，退出程序", e);
            System.exit(1);
        }
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        bossChannel.close().awaitUninterruptibly();
    }

}