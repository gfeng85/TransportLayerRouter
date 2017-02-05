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


public class StreamLinker implements Runnable{
	private static final Logger logger = Logger.getLogger(StartTlr.class);
	public Socket sourceSocket;
	public Socket destSocket;
	public InputStream is;
	public OutputStream os;
	public StreamLinker(Socket sourceSocket,Socket destSocket,InputStream is,OutputStream os)
	{
		this.sourceSocket=sourceSocket;
		this.destSocket=destSocket;
		this.is=is;
		this.os=os;
	}
	public void run()
	{
		int len=0;
		byte []buf=new byte[1000];
		try {
			while(!sourceSocket.isClosed()&&!destSocket.isClosed()&&(len=is.read(buf))>0){
				logger.info(new String(buf,0,len));
				os.write(buf,0,len);
			}
		} catch (IOException e) {
			logger.error("destSocket.isClosed():"+destSocket.isClosed());
			logger.error(e);
			e.printStackTrace();
		}finally{
			logger.info("StreamLinker quit:"+Thread.currentThread().getName());
			try {sourceSocket.close();} catch (IOException e) {}
			try {destSocket.close();} catch (IOException e) {}
			
			
		}
		
	}
}