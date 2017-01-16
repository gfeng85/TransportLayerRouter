package com.icbc.tlr.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.icbc.tlr.Constants;

public class Reactor extends Thread{
	private static final Logger logger = Logger.getLogger(StartTlr.class);
	private String[] dest;//destIp,destPort
	private Selector selector;
	public Reactor(Set<Integer> tcpListeringPortSet){
		try {
			this.selector = Selector.open();
			for(int port:tcpListeringPortSet){
				ServerSocketChannel serverChannel = ServerSocketChannel.open();
				serverChannel.configureBlocking(false);
				serverChannel.socket().bind(new InetSocketAddress(port));
				serverChannel.register(selector, SelectionKey.OP_ACCEPT);
				Constants.getTcpmapbyselectablechannel().put(serverChannel, Constants.getTcpmap().get(port));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		this.dest = Constants.getTcpmap().get(tcpListeringPortSet);
	}
	
	public void run(){

		System.out.println("服务端启动成功！");
		try{
		while (true) {
			selector.select();
			Iterator<SelectionKey> ite = this.selector.selectedKeys().iterator();
			while (ite.hasNext()) {
				SelectionKey key = ite.next();
				ite.remove();
				if (key.isAcceptable()) {
					ServerSocketChannel server = (ServerSocketChannel) key.channel();
					SocketChannel channel = server.accept();
					Constants.getTcpmapbyselectablechannel().put(channel,Constants.getTcpmapbyselectablechannel().get(server));
					Constants.getTcpmapbyselectablechannel().remove(server);
					TcpServer ts = new TcpServer(selector,channel);
					
				} else if (key.isReadable()) {
				}
	
			}
	
		}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
		
}
