package net.gfeng.tlr.nio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

public class Packet {
	private String uuid;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	private SocketChannel fromChannel;
	private SocketChannel toChannel;
	private ByteBuffer buffer;
	private CountDownLatch latch = new CountDownLatch(1);
			
	public CountDownLatch getLatch() {
		return latch;
	}
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}
	public SocketChannel getFromChannel() {
		return fromChannel;
	}
	public void setFromChannel(SocketChannel sourceChannel) {
		this.fromChannel = sourceChannel;
	}
	public SocketChannel getToChannel() {
		return toChannel;
	}
	public void setToChannel(SocketChannel destChannel) {
		this.toChannel = destChannel;
	}
	public ByteBuffer getBuffer() {
		return buffer;
	}
	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}


}
