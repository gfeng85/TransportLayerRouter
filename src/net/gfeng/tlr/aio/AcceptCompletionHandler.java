package net.gfeng.tlr.
aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
/**
 * 每个端口对应一个AcceptCompletionHandler实例
 * @author kfzx-gaofeng1
 *
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
	private static final Logger logger = Logger.getLogger(AcceptCompletionHandler.class);
	int sourcePort;
	String destHost;
	int destPort;

	
	private ClientHandler clientHandler;
	public AcceptCompletionHandler(int sourcePort,String destHost,int destPort){
		this.sourcePort=sourcePort;
		this.destHost=destHost;
		this.destPort=destPort;
		
		clientHandler=new ClientHandler();
	}
	@Override
	public void completed(AsynchronousSocketChannel srcChannel, AsynchronousServerSocketChannel attachment) {
		attachment.accept(attachment, this);
		logger.info("localhost:"+sourcePort+"->"+destHost+":"+destPort+"into accept");
		//创建dest连接
		AsynchronousSocketChannel destChannel=null;
		try {
			destChannel = AsynchronousSocketChannel.open();
			CountDownLatch latch=new CountDownLatch(1);
			destChannel.connect(new InetSocketAddress(destHost, destPort), latch, clientHandler);
			latch.await();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		//创建buffer
		ByteBuffer srcBuffer = ByteBuffer.allocate(1024);
		Packet srcPacket=new Packet();
		srcPacket.setFromChannel(srcChannel);
		srcPacket.setToChannel(destChannel);
		srcPacket.setBuffer(srcBuffer);
		srcPacket.setUuid("*sp*");
		logger.info("srcPacketUuid:"+srcPacket.getUuid());
		
		ByteBuffer desBuffer = ByteBuffer.allocate(1024);
		Packet desPacket=new Packet();
		desPacket.setFromChannel(destChannel);
		desPacket.setToChannel(srcChannel);
		desPacket.setBuffer(desBuffer);
		desPacket.setUuid("*dp*");
		logger.info("destPacketUuid:"+desPacket.getUuid());
		
		logger.info("package create finished!");
		srcChannel.read(srcBuffer, srcPacket, StartTlrAio.readCompletionHandler);
		destChannel.read(desBuffer, desPacket, StartTlrAio.readCompletionHandler);
	}
	
	
	

	@Override
	public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
		exc.printStackTrace();
		StartTlrAio.latch.countDown();
	}

}
