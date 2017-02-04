package com.icbc.tlr.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.apache.log4j.Logger;

import com.icbc.tlr.Constants;
/**
 * 向packet的dest写入数据后，再从dest读取数据，并转换packet的dest和souce
 * @author kfzx-gaofeng1
 *
 */
public class WriteCompletionHandler implements CompletionHandler<Integer, Packet> {
	private static final Logger logger = Logger.getLogger(ReadCompletionHandler.class);
	private int sourcePort;
	private ReadCompletionHandler readCompletionHandler;
	@Override
	public void completed(Integer result, Packet attachment) {
		
		// 如果没有发送完成，继续发送
		try{
			while(attachment.getBuffer().hasRemaining()&& attachment.getToChannel().isOpen())
				attachment.getToChannel().write(attachment.getBuffer(), attachment, this);
			logger.info(attachment.getUuid()+ " write complete!");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			attachment.getBuffer().clear();
			if(attachment.getFromChannel().isOpen())attachment.getFromChannel().read(attachment.getBuffer(), attachment, StartTlrAio.readCompletionHandler);
		}
	}
	
	@Override
	public void failed(Throwable exc, Packet attachment) {
		try {
			attachment.getToChannel().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
