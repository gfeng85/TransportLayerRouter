package net.gfeng.tlr.
bio;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import net.gfeng.tlr.Constants;

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

	public static void main(String[] args) {
		Set<Thread> threadSet=new HashSet<Thread>();
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
						TcpReactor runable = new TcpReactor(Integer.parseInt(cols[1]));
						Thread thread = new Thread(runable,cols[1]+" listener");
						thread.start();
						threadSet.add(thread);
					}else if("udp".equals(cols[0])){
						String[] s={cols[2],cols[3]};
						Constants.getUdpmap().put(Integer.parseInt(cols[1]), s);
					}else{}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(threadSet.size()>0){
			for(Thread i:threadSet){
				if(!i.isAlive()){
					threadSet.remove(i);
					logger.info("remove "+i.getName());
				}
				
			}
//			logger.info("curr Reactor Thread Count:"+threadSet.size());
			try {Thread.sleep(5000);} catch (InterruptedException e) {}
		}
		
		
		
		
	}

}
