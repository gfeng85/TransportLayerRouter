package com.icbc.tlr.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CountDownLatch;

public class Packet {
	private String uuid;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	private AsynchronousSocketChannel fromChannel;
	private AsynchronousSocketChannel toChannel;
	private ByteBuffer buffer;
	private CountDownLatch latch = new CountDownLatch(1);
			
	public CountDownLatch getLatch() {
		return latch;
	}
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}
	public AsynchronousSocketChannel getFromChannel() {
		return fromChannel;
	}
	public void setFromChannel(AsynchronousSocketChannel sourceChannel) {
		this.fromChannel = sourceChannel;
	}
	public AsynchronousSocketChannel getToChannel() {
		return toChannel;
	}
	public void setToChannel(AsynchronousSocketChannel destChannel) {
		this.toChannel = destChannel;
	}
	public ByteBuffer getBuffer() {
		return buffer;
	}
	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}


}
