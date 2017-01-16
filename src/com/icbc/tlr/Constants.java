package com.icbc.tlr;

import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;
import java.util.Map;

public class Constants {
	private static final Map<Integer,String[]> tcpMap=new HashMap<Integer,String[]>();
	private static final Map<Integer,String[]> udpMap=new HashMap<Integer,String[]>();
	private static final Map<SelectableChannel,String[]> tcpMapBySelectableChannel=new HashMap<SelectableChannel,String[]>();
	private static final Map<SelectableChannel,String[]> udpMapBySelectableChannel=new HashMap<SelectableChannel,String[]>();
	public static Map<SelectableChannel, String[]> getTcpmapbyselectablechannel() {
		return tcpMapBySelectableChannel;
	}
	public static Map<SelectableChannel, String[]> getUdpmapbyselectablechannel() {
		return udpMapBySelectableChannel;
	}
	public static Map<Integer, String[]> getTcpmap() {
		return tcpMap;
	}
	public static Map<Integer, String[]> getUdpmap() {
		return udpMap;
	}
}
