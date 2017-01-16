package com.icbc.tlr.aio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;


public class UdpServer implements Runnable{
	private static final Logger logger = Logger.getLogger(UdpServer.class);
	public String[] src;
	public String[] dest;
	DatagramSocket sourceSocket;
	DatagramSocket destSocket;
	public UdpServer(String [] src,String [] dest)
	{
		this.src=src;
		this.dest=dest;
	}
	public void run()
	{}
	public void processPacket(DatagramPacket sourceRecvPacket) {
			try {
				if(destSocket==null) destSocket = new DatagramSocket();
				DatagramPacket destSendPacket = new DatagramPacket(sourceRecvPacket.getData() ,sourceRecvPacket.getLength() , InetAddress.getByName(dest[0]), Integer.parseInt(dest[1]));
				destSocket.send(destSendPacket);
				byte[] recvBuf = new byte[100];
		        DatagramPacket destRecvPacket = new DatagramPacket(recvBuf , recvBuf.length);
		        destSocket.receive(destRecvPacket);
		        
		        if(sourceSocket==null) sourceSocket = new DatagramSocket();
		        DatagramPacket sourceSendPacket = new DatagramPacket(sourceRecvPacket.getData() ,sourceRecvPacket.getLength() , InetAddress.getByName(dest[0]), Integer.parseInt(dest[1]));
		        sourceSocket.send(sourceSendPacket);;
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
}