package com.icbc.tlr.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.icbc.tlr.Constants;

public class TcpReactor implements Runnable{
	private static final Logger logger = Logger.getLogger(StartTlr.class);
	private int listerningPort=0;
	private String[] dest;//destIp,destPort
	public TcpReactor(int port){
		this.listerningPort=port;
		this.dest = Constants.getTcpmap().get(port);
	}
	
	public void run(){
		ServerSocket sourceServerSock=null;
		try {
			sourceServerSock=new ServerSocket();
			sourceServerSock.setReuseAddress(true);
			sourceServerSock.bind(new InetSocketAddress(listerningPort));
			
			int i=0;
			while(true)
			{
				logger.info("TcpReactor is started. Lisening Port:"+listerningPort);
				Socket sourceSock=sourceServerSock.accept();
				TcpServer ts = new TcpServer(sourceSock,dest);
				new Thread(ts,"TcpServer"+listerningPort+"_"+i++).start();
			}
		} catch (Exception e) {
			logger.info("err on port:"+listerningPort);
			e.printStackTrace();
		}finally{
			try {sourceServerSock.close();} catch (IOException e) {}
		}
	}
}
