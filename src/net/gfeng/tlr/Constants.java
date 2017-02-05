package net.gfeng.tlr;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Constants {
	private static final Map<Integer,String[]> tcpMap=new HashMap<Integer,String[]>();
	private static final Map<Integer,String[]> udpMap=new HashMap<Integer,String[]>();
	private static final ThreadPoolExecutor threadpool=new ThreadPoolExecutor(20, 100, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10), new ThreadPoolExecutor.DiscardOldestPolicy());
	public static ThreadPoolExecutor getThreadpool() {
		return threadpool;
	}
	public static Map<Integer, String[]> getTcpmap() {
		return tcpMap;
	}
	public static Map<Integer, String[]> getUdpmap() {
		return udpMap;
	}
}
