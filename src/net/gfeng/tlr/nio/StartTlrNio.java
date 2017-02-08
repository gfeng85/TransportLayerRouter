package net.gfeng.tlr.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.log4j.Logger;

import net.gfeng.tlr.Constants;

public class StartTlrNio {
	private static final Logger logger = Logger.getLogger(StartTlrNio.class);
	public static void main(String[] args) {
		InputStream is = StartTlrNio.class.getClassLoader().getResourceAsStream("route.cfg");
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		Selector selector = null;
		try {
			selector = Selector.open();
			String line=br.readLine();//读取首行
			while ((line=br.readLine())!=null){
				String[] cols = line.split(",",-1);
				if(cols.length>=4){
					System.out.println(cols[1]);
					if("tcp".equals(cols[0])){
						String[] s={cols[2],cols[3]};
						Constants.getTcpmap().put(Integer.parseInt(cols[1]),s);
						//监听端口
						ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
						serverSocketChannel.configureBlocking(false);
						serverSocketChannel.socket().bind(new InetSocketAddress(Integer.parseInt(cols[1])));
						logger.info("Lisening Port:"+cols[1]);
						if(serverSocketChannel.isOpen())
							serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
						else
							logger.error("serverChannel is not open!");
					}else if("udp".equals(cols[0])){
						String[] s={cols[2],cols[3]};
						Constants.getUdpmap().put(Integer.parseInt(cols[1]), s);
					}else{}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try{
			while (true) {
				selector.select();
				Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
				while (ite.hasNext()) {
					SelectionKey key = ite.next();
					ite.remove();
					if (key.isAcceptable()) {
						logger.info("into accept");
						ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
						//获取源通道
						SocketChannel sourceChannel = serverSocketChannel.accept();
						sourceChannel.configureBlocking(false);
						
						
						//创建目标端通道
						SocketChannel destSocketChannel = SocketChannel.open();
						destSocketChannel.configureBlocking(false);
						String[] s = Constants.getTcpmap().get(serverSocketChannel.socket().getLocalPort());
						destSocketChannel.connect(new InetSocketAddress(s[0],Integer.parseInt(s[1])));
						Packet srcPacket=new Packet();
						
						ByteBuffer srcBuffer = ByteBuffer.allocate(1024);
						srcPacket.setFromChannel(sourceChannel);
						srcPacket.setToChannel(destSocketChannel);
						srcPacket.setBuffer(srcBuffer);
						srcPacket.setUuid("*sp*");
						logger.info("srcPacketUuid:"+srcPacket.getUuid());
						
						ByteBuffer desBuffer = ByteBuffer.allocate(1024);
						Packet desPacket=new Packet();
						desPacket.setFromChannel(destSocketChannel);
						desPacket.setToChannel(sourceChannel);
						desPacket.setBuffer(desBuffer);
						desPacket.setUuid("*dp*");
						logger.info("destPacketUuid:"+desPacket.getUuid());
						
						logger.info("package create finished!");
						sourceChannel.register(selector, SelectionKey.OP_READ,srcPacket);
						destSocketChannel.register(selector, SelectionKey.OP_CONNECT,desPacket);
					} else if (key.isConnectable()){
						logger.info("into connect");
						Packet attachment=(Packet) key.attachment();
						SocketChannel destSocketChannel = (SocketChannel) key.channel();
						if(destSocketChannel.isConnectionPending()){
							destSocketChannel.finishConnect();
						}
						destSocketChannel.configureBlocking(false);
						destSocketChannel.register(selector, SelectionKey.OP_READ,attachment);
					} else if (key.isReadable()) {
						logger.info("into read");
						Packet attachment=(Packet) key.attachment();
						if(attachment.getToChannel()!=null && attachment.getToChannel().isConnected()){
							ByteBuffer buffer = ByteBuffer.allocate(1024);
							int readResult=attachment.getFromChannel().read(buffer);
							String tmp = new String(buffer.array());
							logger.info(tmp);
							if(readResult<0){
								try{attachment.getFromChannel().close();}catch(IOException e) {}
								try{attachment.getToChannel().close();}catch(IOException e) {}
							}
							buffer.flip();
							while(buffer.hasRemaining()
									&&readResult>0
									&&attachment.getToChannel().isOpen()){
								attachment.getToChannel().write(buffer);
							}
						}
					}
		
				}
		
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
