package net.gfeng.tlr.
bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;


public class TcpServer implements Runnable{
	private static final Logger logger = Logger.getLogger(StartTlr.class);
	public Socket sourceSocket;
	public String[] dest;
	public TcpServer(Socket sourceSock,String [] dest)
	{
		this.sourceSocket=sourceSock;
		this.dest=dest;
	}
	public void run()
	{
		logger.info("Thread:"+Thread.currentThread().getName()+" processing");
		OutputStream sourceOs = null;
		InputStream sourceIs = null;
		OutputStream destOs = null;
		InputStream destIs = null;
		Socket destSocket = null;
		try{
			sourceOs = sourceSocket.getOutputStream();
			sourceIs = sourceSocket.getInputStream();
			destSocket = new Socket(InetAddress.getByName(dest[0]),Integer.parseInt(dest[1]));
			destOs=destSocket.getOutputStream();
			destIs=destSocket.getInputStream();
			
			StreamLinker sls = new StreamLinker(sourceSocket,destSocket,sourceIs,destOs);
			Thread slsThread = new Thread(sls,Thread.currentThread().getName()+"_req");
			slsThread.start();
			StreamLinker sld = new StreamLinker(sourceSocket,destSocket,destIs,sourceOs);
			Thread sldThread = new Thread(sld,Thread.currentThread().getName()+"_rep");
			sldThread.start();
			while(slsThread.isAlive()||sldThread.isAlive()){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}catch (IOException e1) {
			e1.printStackTrace();
		}finally{
			try {sourceSocket.close();} catch (IOException e) {}
			try {destSocket.close();} catch (IOException e) {}
		}
	}
}