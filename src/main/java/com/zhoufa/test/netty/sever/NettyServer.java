package com.zhoufa.test.netty.sever;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

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
