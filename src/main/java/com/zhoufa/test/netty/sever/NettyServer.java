package com.zhoufa.test.netty.sever;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/***
 * ┌───┐   ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┐
 * │Esc│   │ F1│ F2│ F3│ F4│ │ F5│ F6│ F7│ F8│ │ F9│F10│F11│F12│ │P/S│S L│P/B│  ┌┐    ┌┐    ┌┐
 * └───┘   └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┘  └┘    └┘    └┘
 * ┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───────┐ ┌───┬───┬───┐ ┌───┬───┬───┬───┐
 * │~ `│! 1│@ 2│# 3│$ 4│% 5│^ 6│& 7│* 8│( 9│) 0│_ -│+ =│ BacSp │ │Ins│Hom│PUp│ │N L│ / │ * │ - │
 * ├───┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─────┤ ├───┼───┼───┤ ├───┼───┼───┼───┤
 * │ Tab │ Q │ W │ E │ R │ T │ Y │ U │ I │ O │ P │{ [│} ]│ | \ │ │Del│End│PDn│ │ 7 │ 8 │ 9 │   │
 * ├─────┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴─────┤ └───┴───┴───┘ ├───┼───┼───┤ + │
 * │ Caps │ A │ S │ D │ F │ G │ H │ J │ K │ L │: ;│" '│ Enter  │               │ 4 │ 5 │ 6 │   │
 * ├──────┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴────────┤     ┌───┐     ├───┼───┼───┼───┤
 * │ Shift  │ Z │ X │ C │ V │ B │ N │ M │< ,│> .│? /│  Shift   │     │ ↑ │     │ 1 │ 2 │ 3 │   │
 * ├─────┬──┴─┬─┴──┬┴───┴───┴───┴───┴───┴──┬┴───┼───┴┬────┬────┤ ┌───┼───┼───┐ ├───┴───┼───┤ E││
 * │ Ctrl│    │Alt │         Space         │ Alt│    │    │Ctrl│ │ ← │ ↓ │ → │ │   0   │ . │←─┘│
 * └─────┴────┴────┴───────────────────────┴────┴────┴────┴────┘ └───┴───┴───┘ └───────┴───┴───┘
 * bootstrap 配置并启动服务的类
 * buffer   缓冲相关类，对NIO Buffer做了一些封装
 * channel  核心部分，处理连接
 * container    连接其他窗口的代码
 * example  使用示例，在正式版本里面没有
 * handler  基于handler的扩展部分，实现土方编解码等附加功能
 * logging  日志
 * util 工具
 */
public class NettyServer {
    final static int port = 8080;

    public static void main(String[] args) {
        Server server = new Server();
        server.config();
        server.start();

    }

    static class Server {

        ServerBootstrap bootstrap;

        Channel parentChannel;

        InetSocketAddress localAddress;

        MyChannelHandler channelHandler = new MyChannelHandler();

        Server() {
            bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
            bootstrap.setOption("reuseAddress", true);
            bootstrap.setOption("child.tcpNoDelay", true);
            bootstrap.setOption("child.soLinger", 2);
            bootstrap.getPipeline().addLast("servercnfactory", channelHandler);
        }

        void config() {
            localAddress = new InetSocketAddress(NettyServer.port);
        }

        void start() {
            parentChannel = bootstrap.bind(localAddress);
        }

        class MyChannelHandler extends SimpleChannelHandler{
            @Override
            public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
                System.out.println("Channel closed " + e);
            }

            @Override
            public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
                System.out.println("Channel connected " + e);
            }

            @Override
            public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
                System.out.println("New message " + e.toString() + " from" +  ctx.getChannel());
                processMsg(e);
            }

            private void processMsg (MessageEvent e) {
                Channel channel = e.getChannel();
                channel.write(e.getMessage());
            }
        }
    }
}
