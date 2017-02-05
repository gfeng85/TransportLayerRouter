package net.gfeng.tlr.
aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.apache.log4j.Logger;

import net.gfeng.tlr.Constants;
/**
 * 从packet的sourceChannel读取数据后，写入destChannel
 * @author kfzx-gaofeng1
 *
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, Packet> {
	private static final Logger logger = Logger.getLogger(ReadCompletionHandler.class);
	@Override
	public void completed(Integer result, Packet attachment) {
		String tmp = new String(attachment.getBuffer().array());
		attachment.getBuffer().flip();
		if(result<0){
			logger.info(attachment.getUuid()+" socket closed! "+result);
			try {attachment.getFromChannel().close();} catch (IOException e) {}
			try {attachment.getToChannel().close();} catch (IOException e) {}
		}
		else if(attachment.getFromChannel().isOpen()
				&&attachment.getToChannel().isOpen()
				&&result>0){
			logger.info(attachment.getUuid()+" read completed! write to ToChannel");
			logger.info(tmp);
			attachment.getToChannel().write(attachment.getBuffer(), attachment,StartTlrAio.writeCompletionHandler);
		}
		
	}
	
	@Override
	public void failed(Throwable exc, Packet attachment) {
		try {
			attachment.getFromChannel().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
