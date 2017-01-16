package com.icbc.tlr.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.icbc.tlr.Constants;

public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
	private int sourcePort;
	private AsynchronousSocketChannel channel;
	private AsynchronousSocketChannel destChannel=null;
	public ReadCompletionHandler(AsynchronousSocketChannel channel,int sourcePort) {
		this.sourcePort=sourcePort;
		if (this.channel == null)
			this.channel = channel;
	}

	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		try {
			if(destChannel==null){
				String[] s = Constants.getTcpmap().get(sourcePort);
				destChannel = AsynchronousSocketChannel.open();
				destChannel.connect(new InetSocketAddress(s[0], Integer.parseInt(s[1])), destChannel, new AsyncTimeClientHandler());
			}
			attachment.flip();//切换至Read模式
			byte[] body = new byte[attachment.remaining()];
			attachment.get(body);
			String req = new String(body, "UTF-8");
			System.out.println("The time server receive order : " + req);
			String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req)
					? new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
			doWrite(currentTime);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doWrite(String currentTime) {
		if (currentTime != null && currentTime.trim().length() > 0) {
			byte[] bytes = (currentTime).getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
				@Override
				public void completed(Integer result, ByteBuffer buffer) {
					// 如果没有发送完成，继续发送
					if (buffer.hasRemaining())
						channel.write(buffer, buffer, this);
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					try {
						channel.close();
					} catch (IOException e) {
						// ingnore on close
					}
				}
			});
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try {
			this.channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
