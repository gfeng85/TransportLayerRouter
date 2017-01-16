package com.icbc.tlr.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.icbc.tlr.Constants;


public class TcpServer extends Thread{
	private static final Logger logger = Logger.getLogger(StartTlr.class);
	public SocketChannel sourceChannel;
	public String[] dest;
	public TcpServer(Selector selector, SocketChannel channel)
	{
		try {
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.sourceChannel=channel;
		this.dest=Constants.getTcpmapbyselectablechannel().get(channel);
	}
	public void run()
	{
		logger.info("Thread:"+Thread.currentThread().getName()+" processing");

	}
}