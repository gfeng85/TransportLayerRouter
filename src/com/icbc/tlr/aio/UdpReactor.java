package com.icbc.tlr.aio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.icbc.tlr.Constants;

public class UdpReactor {
	
	private static final Logger logger = Logger.getLogger(StartTlr.class);
	
	private int listerningPort=0;
	
	private String[] dest;//destIp,destPort
	
	private Map<String,UdpServer> serverMap=new HashMap<String,UdpServer>();
	
	public UdpReactor(int port){
		this.listerningPort=port;
		this.dest = Constants.getUdpmap().get(port);
	}
	
	public void run(){
		DatagramSocket serverSocket=null;
		try {
			serverSocket = new DatagramSocket(listerningPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
        byte[] recvBuf = new byte[100];
        DatagramPacket recvPacket = new DatagramPacket(recvBuf , recvBuf.length);
        try {
			while(true){
				serverSocket.receive(recvPacket);
		        int sourcePort = recvPacket.getPort();
		        InetAddress sourceAddr = recvPacket.getAddress();
		        UdpServer server=null;
		        if(serverMap.containsKey(sourceAddr+":"+sourcePort)){
		        	server = serverMap.get(sourceAddr+":"+sourcePort);
		        }else{
		        	String[] src={sourceAddr.toString(),Integer.toString(sourcePort)};
		        	server = new UdpServer(src,dest);
		        }
		        server.processPacket(recvPacket);
				
			}
        } catch (IOException e) {
			e.printStackTrace();
		}finally{
			serverSocket.close();
		}
	}
}
