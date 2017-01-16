package com.icbc.tlr.aio;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.icbc.tlr.Constants;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class StartTlr {
	private static final Logger logger = Logger.getLogger(StartTlr.class);
	public static final CountDownLatch latch = new CountDownLatch(1);
	public static void main(String[] args) {
		Set<AsynchronousServerSocketChannel> channelSet=new HashSet<AsynchronousServerSocketChannel>();
		InputStream is = StartTlr.class.getClassLoader().getResourceAsStream("route.cfg");
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		try {
			String line=br.readLine();//¶ÁÈ¡Ê×ÐÐ
			while ((line=br.readLine())!=null){
				String[] cols = line.split(",",-1);
				if(cols.length>=4){
					if("tcp".equals(cols[0])){
						String[] s={cols[2],cols[3]};
						Constants.getTcpmap().put(Integer.parseInt(cols[1]),s);
						AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
						asynchronousServerSocketChannel.bind(new InetSocketAddress(Integer.parseInt(cols[1])));
						asynchronousServerSocketChannel.accept(asynchronousServerSocketChannel, new AcceptCompletionHandler(Integer.parseInt(cols[1])));
//					}else if("udp".equals(cols[0])){
//						String[] s={cols[2],cols[3]};
//						Constants.getUdpmap().put(Integer.parseInt(cols[1]), s);
					}else{}
				}
			}
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}

}
