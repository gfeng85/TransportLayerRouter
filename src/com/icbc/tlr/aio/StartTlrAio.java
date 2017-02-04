package com.icbc.tlr.aio;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.net.SocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.icbc.tlr.Constants;

public class StartTlrAio {
	private static final Logger logger = Logger.getLogger(StartTlrAio.class);
	public static final CountDownLatch latch = new CountDownLatch(1);
	public static final ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler();
	public static final WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler();
	public static void main(String[] args) {
		AsynchronousChannelGroup asynchronousChannelGroup = null;
		InputStream is = StartTlrAio.class.getClassLoader().getResourceAsStream("route.cfg");
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		try {
			asynchronousChannelGroup = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 100);
			String line=br.readLine();//读取首行
			while ((line=br.readLine())!=null){
				String[] cols = line.split(",",-1);
				if(cols.length>=4){
					System.out.println(cols[1]);
					if("tcp".equals(cols[0])){
						String[] s={cols[2],cols[3]};
						Constants.getTcpmap().put(Integer.parseInt(cols[1]),s);
						AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open(asynchronousChannelGroup);
						asynchronousServerSocketChannel.bind(new InetSocketAddress(Integer.parseInt(cols[1])),100);//100用于指定等待连接的队列大小(backlog)
						logger.info("Lisening Port:"+cols[1]);
						if(asynchronousServerSocketChannel.isOpen())
							asynchronousServerSocketChannel.accept(asynchronousServerSocketChannel, new AcceptCompletionHandler(Integer.parseInt(cols[1]),cols[2],Integer.parseInt(cols[3])));
						else
							logger.error("asynchronousServerSocketChannel is not open!");
					}else if("udp".equals(cols[0])){
						String[] s={cols[2],cols[3]};
						Constants.getUdpmap().put(Integer.parseInt(cols[1]), s);
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
