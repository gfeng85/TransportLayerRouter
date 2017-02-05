package net.gfeng.tlr.
aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

public class ClientHandler implements CompletionHandler<Void, CountDownLatch> {
	private static final Logger logger = Logger.getLogger(ClientHandler.class);
	private AsynchronousSocketChannel client;
	private CountDownLatch latch;


	@Override
	public void completed(Void result, CountDownLatch attachment) {
		logger.info("ClientHandler completed!");
		attachment.countDown();
	}

	@Override
	public void failed(Throwable exc, CountDownLatch attachment) {
		exc.printStackTrace();
		try {
			client.close();
			latch.countDown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
